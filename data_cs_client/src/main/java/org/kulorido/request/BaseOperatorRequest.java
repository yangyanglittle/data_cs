package org.kulorido.request;

import lombok.Data;

/**
 * @package org.kulorido.request
 * @Author kulorido
 * @Data 2023/6/9 14:00
 */
@Data
public class BaseOperatorRequest {

    public BaseOperatorRequest(){}

    public BaseOperatorRequest(String operator){
        this.operator = operator;
    }

    private String operator;

}
