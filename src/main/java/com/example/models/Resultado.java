package com.example.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Resultado {
    private Boolean error;
    private Boolean resultado1;
    private String mensaje;
    private String tag;
    private Integer codigoError;
    private byte[] datos;
}