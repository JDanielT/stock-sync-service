package br.com.daniel.stocksyncservice.response.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ErrorResponse implements Serializable {

    private String code;
    private String message;
    private String details;
}
