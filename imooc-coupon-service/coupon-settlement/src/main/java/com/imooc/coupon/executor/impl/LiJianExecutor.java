package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 立减优惠券结算规则执行器
 */
@Slf4j
@Component
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {

    /**
     * 规则类型标记
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
    }

    /**
     * 优惠券规则的计算
     *
     * @param settlement {@link SettlementInfo} 包含了选择的优惠券
     * @return {@link SettlementInfo} 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {

        double goodsSum = retain2Decimal(goodCostSum(
                settlement.getGoodsInfos()
        ));

        SettlementInfo probability = processGoddsTypeNotSatisfy(
                settlement, goodsSum
        );
        if(null != probability){
            log.debug("LiJian Template Is Not Match To GoodsType!");
            return probability;
        }

        //立减优惠券直接使用，没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndTemplateInfos()
                .get(0).getTemplate();
        double quota = templateSDK.getRule().getDiscount().getQuota();

        //计算使用优惠券之后的价格 --结算
        settlement.setCost(
                retain2Decimal(goodsSum - quota) > minCost() ?
                        retain2Decimal(goodsSum - quota) : minCost()
        );

        log.debug("Use LiJian Coupon Make Goods Cost From {} TO {}",
                goodsSum,settlement.getCost());

        return settlement;
    }
}
