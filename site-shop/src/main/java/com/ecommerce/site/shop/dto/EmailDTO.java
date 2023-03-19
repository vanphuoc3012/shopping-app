package com.ecommerce.site.shop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailDTO {
    private String toEmail;
    private String subject;
    private String content;
    private boolean html;
}
