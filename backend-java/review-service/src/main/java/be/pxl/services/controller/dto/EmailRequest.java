package be.pxl.services.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {
    private String email;
    private String subject;
    private String body;
}
