package com.lly835.bestpay.service.impl;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.SignType;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayResultEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.exception.BestPayException;
import com.lly835.bestpay.model.*;
import com.lly835.bestpay.model.wxpay.response.WxQrCode2WxResponse;
import com.lly835.bestpay.model.wxpay.response.WxQrCodeAsyncResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.alipay.AliPayServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

public class BestPayServiceImpl implements BestPayService {

    /**
     *  TODO 重构
     * 暂时先再引入一个config
     */
    private WxPayConfig wxPayConfig;
    private AliPayConfig aliPayConfig;

    public void setWxPayConfig(WxPayConfig wxPayConfig) {
        this.wxPayConfig = wxPayConfig;
    }

    public void setAliPayConfig(AliPayConfig aliPayConfig) {
        this.aliPayConfig = aliPayConfig;
    }

    @Override
    public PayResponse pay(PayRequest request) {
        Objects.requireNonNull(request,"request params must not be null");
        //微信支付
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);
        // 支付宝网站PC支付
        AliPayServiceImpl aliPayService = new AliPayServiceImpl();
        aliPayService.setAliPayConfig(aliPayConfig);
        switch(request.getPayTypeEnum()) {
            case WXPAY_H5:
                //微信h5支付
                return wxPayService.pay(request);
            case WXPAY_MINI:
                //微信小程序支付
                return wxPayService.pay(request);
            case ALIPAY_PC:
                // 支付宝网站PC支付
                return aliPayService.pay(request);
            default:
              break;
        }
        return null;

    }

    /**
     * 同步返回
     *
     * @param request
     * @return
     */
    public PayResponse syncNotify(HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean verify(Map<String, String> toBeVerifiedParamMap, SignType signType, String sign) {
        return false;
    }

    /**
     * 异步回调
     *
     * @return
     */
    public PayResponse asyncNotify(String notifyData) {

//        //微信h5支付
//        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
//        wxPayService.setWxPayConfig(this.wxPayConfig);
//        return wxPayService.asyncNotify(notifyData);
        //  支付宝PC支付
        AliPayServiceImpl aliPayService = new AliPayServiceImpl();
        aliPayService.setAliPayConfig(aliPayConfig);
        return aliPayService.asyncNotify(notifyData);
    }

    /**
     * 判断是什么支付类型(从同步回调中获取参数)
     *
     * @param request
     * @return
     */
    private BestPayTypeEnum payType(HttpServletRequest request) {
        //先判断是微信还是支付宝 是否是xml
        //支付宝同步还是异步
        if (request.getParameter("notify_type") == null) {
            //支付宝同步
            if (request.getParameter("exterface") != null && request.getParameter("exterface").equals("create_direct_pay_by_user")) {
                return BestPayTypeEnum.ALIPAY_PC;
            }
            if (request.getParameter("method") != null && request.getParameter("method").equals("alipay.trade.wap.pay.return")) {
                return BestPayTypeEnum.ALIPAY_WAP;
            }
        } else {
            //支付宝异步(发起支付时使用这个参数标识支付方式)
            String payType = request.getParameter("passback_params");
            return BestPayTypeEnum.getByCode(payType);
        }

        throw new BestPayException(BestPayResultEnum.PAY_TYPE_ERROR);
    }

    @Override
    public RefundResponse refund(RefundRequest request) {
        //微信h5支付
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);
        return wxPayService.refund(request);
    }

    /**
     * 查询订单
     *
     * @param request
     * @return
     */
    @Override
    public OrderQueryResponse query(OrderQueryRequest request) {
        //微信h5支付
        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);

        return wxPayService.query(request);
    }

    @Override
    public String downloadBill(DownloadBillRequest request) {

        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);


        return wxPayService.downloadBill(request);
    }

    @Override
    public String getQrCodeUrl(String productId) {

        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);

        return wxPayService.getQrCodeUrl(productId);
    }

    @Override
    public WxQrCodeAsyncResponse asyncQrCodeNotify(String notifyData) {

        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);

        return wxPayService.asyncQrCodeNotify(notifyData);
    }

    public WxQrCode2WxResponse buildQrCodeResponse(PayResponse payResponse) {

        WxPayServiceImpl wxPayService = new WxPayServiceImpl();
        wxPayService.setWxPayConfig(this.wxPayConfig);

        return wxPayService.buildQrCodeResponse(payResponse);

    }
}