package com.iwxyi.fairyland.server.Models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 标记持久化类，自动建表 user。若不同可用 @Table(name="")
@Data // 自动生成get set equals hashCode toString等
@NoArgsConstructor
public class DailyPersist {
    @Id
    @NotNull
    private Long userId;
    
    @Min(0)
    @Max(65565)
    private int dwc0 = 0;
    @Min(0)
    @Max(65565)
    private int dwc1 = 0;
    @Min(0)
    @Max(65565)
    private int dwc2 = 0;
    @Min(0)
    @Max(65565)
    private int dwc3 = 0;
    @Min(0)
    @Max(65565)
    private int dwc4 = 0;
    @Min(0)
    @Max(65565)
    private int dwc5 = 0;
    @Min(0)
    @Max(65565)
    private int dwc6 = 0;
    @Min(0)
    @Max(65565)
    private int dwc7 = 0;
    
    public DailyPersist(Long userId) {
        this.userId = userId;
    }
}
