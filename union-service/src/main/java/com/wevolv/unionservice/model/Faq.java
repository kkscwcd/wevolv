package com.wevolv.unionservice.model;

import com.wevolv.unionservice.model.dto.FaqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;

import static org.apache.http.util.Asserts.check;
import static org.apache.http.util.Asserts.notBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j

public class Faq {
    @Id
    private String id;
    private String categoryId;
    private String title;
    private String description;

    public static void requestValidator(FaqDto faqDto){
        try{
            notBlank(faqDto.getCategoryId(), "category must be set and cannot be blank");
        }catch(Exception e){
            log.warn("Registration fields not correct {} {}", faqDto, e.getMessage());
            throw e;
        }
    }

}
