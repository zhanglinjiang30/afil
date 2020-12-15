package com.liuqi.base;

import com.liuqi.business.dto.ColumnInfo;
import com.liuqi.business.dto.TableInfo;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.model.TrusteeModelDto;
import com.liuqi.business.service.GenerateService;
import com.liuqi.business.service.TrusteeService;
import com.liuqi.business.trade.TradeRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CodeTest extends BaseTest {


    @Autowired
    private GenerateService generateService;


    @Test
    public void tables(){
        List<TableInfo> tables= generateService.getAllTableName();
        System.out.println(tables);
    }
    @Test
    public void colums(){
        //List<ColumnInfo> colums= generateService.listTableColumn("btc_address");
        //System.out.println(colums);
    }

}
