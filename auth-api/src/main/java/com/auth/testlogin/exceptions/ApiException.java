package com.auth.testlogin.exceptions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "ApiException details")
public class ApiException {


    private Integer status;

    /**
     * displays date and hour
     */
    @ApiModelProperty(value = "Display of date and hour")
    private String timeStamp;

    /**
     * exception message
     */
    @ApiModelProperty(value = "Display of exception message")
    private String message;

    /**
     * exception details
     */
    @ApiModelProperty(value = "Display of exception details")
    private String details;

    public ApiException(Integer status, Date timeStamp, String message, String details) {
        this.status = status;
        this.timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timeStamp);
        this.message = message;
        this.details = details;
    }
}
