package ec.edu.ups.icc.fundamentos01.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos permitidos para actualizar parcialmente a un usuario")
public class PartialUpdateUserDto {

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    @Size(min = 3, max = 150)
    private String name;

    @Schema(description = "Correo institucional o personal actualizado", example = "jperez@ups.edu.ec")
    @Email
    @Size(max = 150)
    private String email;

    @Schema(description = "Nueva contraseña del usuario", example = "NuevaPass123!")
    @Size(min = 8)
    private String password;


    
    public PartialUpdateUserDto() {
    }

    public PartialUpdateUserDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
