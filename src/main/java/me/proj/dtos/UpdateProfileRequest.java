package me.proj.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.proj.entities.User;

@Data
public class UpdateProfileRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 7)
    private String color;

    @Size(min = 4, max = 100)
    private String newPassword;

    public static UpdateProfileRequest from(User user) {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName(user.getName());
        request.setColor(user.getColor());
        return request;
    }
}
