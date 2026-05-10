package com.ttcs.ttcs_app.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLikeId implements Serializable {
    private String user;   
    private String product;
}