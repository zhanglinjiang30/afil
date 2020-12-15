package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CoinArticleModelDto extends CoinArticleModel {


    @JsonIgnore
    private String sortName="aid";

    @JsonIgnore
    private String sortType="desc";

    private String timeString;

}
