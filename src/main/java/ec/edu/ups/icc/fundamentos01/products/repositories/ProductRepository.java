package ec.edu.ups.icc.fundamentos01.products.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

        Optional<ProductEntity> findByNameIgnoreCaseAndDeletedFalse(String name);

        List<ProductEntity> findByDeletedFalse();

        Optional<ProductEntity> findByIdAndDeletedFalse(Long id);

        List<ProductEntity> findByOwner_IdAndDeletedFalse(Long ownerId);

        /*
         * Busca productos activos de un usuario aplicando filtros opcionales.
         */
        @Query("""
                SELECT p
                FROM ProductEntity p
                WHERE p.deleted = false
                AND p.owner.id = :userId
                AND p.owner.deleted = false
                AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                AND (:minPrice IS NULL OR p.price >= :minPrice)
                AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                """)
        List<ProductEntity> findByOwnerIdWithFilters(
                @Param("userId") Long userId,
                @Param("name") String name,
                @Param("minPrice") Double minPrice,
                @Param("maxPrice") Double maxPrice
        );

        /*
         * Busca productos activos de una categoría aplicando filtros opcionales.
         * (Punto 18: Usa JOIN y DISTINCT por la nueva relación ManyToMany)
         */
        @Query("""
                SELECT DISTINCT p
                FROM ProductEntity p
                JOIN p.categories c
                WHERE p.deleted = false
                AND c.id = :categoryId
                AND c.deleted = false
                AND p.owner.deleted = false
                AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                AND (:minPrice IS NULL OR p.price >= :minPrice)
                AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                AND (:userId IS NULL OR p.owner.id = :userId)
                """)
        List<ProductEntity> findByCategoryIdWithFilters(
                @Param("categoryId") Long categoryId,
                @Param("name") String name,
                @Param("minPrice") Double minPrice,
                @Param("maxPrice") Double maxPrice,
                @Param("userId") Long userId
        );


        // ====================================================================
        // CONSULTAS DE PAGINACIÓN ACTUALIZADAS PARA OWNERSHIP (Práctica 13)
        // ====================================================================

        /*
         * Consulta productos activos usando Page.
         * Incorpora Ownership: Si es ADMIN trae todos, si es USER trae solo los suyos.
         */
        @Query(
                value = """
                        SELECT p
                        FROM ProductEntity p
                        WHERE p.deleted = false
                        AND (:isAdmin = true OR p.owner.id = :userId)
                        """,
                countQuery = """
                        SELECT COUNT(p)
                        FROM ProductEntity p
                        WHERE p.deleted = false
                        AND (:isAdmin = true OR p.owner.id = :userId)
                        """
        )
        Page<ProductEntity> findActivePageWithOwnership(
                @Param("userId") Long userId, 
                @Param("isAdmin") boolean isAdmin, 
                Pageable pageable);

        /*
         * Consulta productos activos usando Slice.
         * Incorpora Ownership: Si es ADMIN trae todos, si es USER trae solo los suyos.
         */
        @Query("""
                SELECT p
                FROM ProductEntity p
                WHERE p.deleted = false
                AND (:isAdmin = true OR p.owner.id = :userId)
                """)
        Slice<ProductEntity> findActiveSliceWithOwnership(
                @Param("userId") Long userId, 
                @Param("isAdmin") boolean isAdmin, 
                Pageable pageable);

        // ====================================================================


        /*
         * Consulta productos por categoría usando Page (Apartado 20.4)
         */
        @Query(
                value = """
                        SELECT DISTINCT p
                        FROM ProductEntity p
                        JOIN p.categories c
                        WHERE p.deleted = false
                                AND c.id = :categoryId
                                AND c.deleted = false
                                AND p.owner.deleted = false
                                AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                                AND (:minPrice IS NULL OR p.price >= :minPrice)
                                AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                                AND (:userId IS NULL OR p.owner.id = :userId)
                        """,
                countQuery = """
                        SELECT COUNT(DISTINCT p)
                        FROM ProductEntity p
                        JOIN p.categories c
                        WHERE p.deleted = false
                                AND c.id = :categoryId
                                AND c.deleted = false
                                AND p.owner.deleted = false
                                AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                                AND (:minPrice IS NULL OR p.price >= :minPrice)
                                AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                                AND (:userId IS NULL OR p.owner.id = :userId)
                        """
        )
        Page<ProductEntity> findByCategoryIdWithFiltersPage(
                @Param("categoryId") Long categoryId,
                @Param("name") String name,
                @Param("minPrice") Double minPrice,
                @Param("maxPrice") Double maxPrice,
                @Param("userId") Long userId,
                Pageable pageable
        );

        @Query("""
                SELECT DISTINCT p
                FROM ProductEntity p
                JOIN p.categories c
                WHERE p.deleted = false
                        AND c.id = :categoryId
                        AND c.deleted = false
                        AND p.owner.deleted = false
                        AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
                        AND (:minPrice IS NULL OR p.price >= :minPrice)
                        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
                        AND (:userId IS NULL OR p.owner.id = :userId)
                """)
        Slice<ProductEntity> findByCategoryIdWithFiltersSlice(
                @Param("categoryId") Long categoryId,
                @Param("name") String name,
                @Param("minPrice") Double minPrice,
                @Param("maxPrice") Double maxPrice,
                @Param("userId") Long userId,
                Pageable pageable
        );

}