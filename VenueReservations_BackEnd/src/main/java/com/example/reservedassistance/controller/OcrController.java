package com.example.reservedassistance.controller;


import com.aliyun.ocr_api20210707.models.RecognizeIdcardResponse;
import com.aliyun.ocr_api20210707.models.RecognizeIdcardResponseBody;


import com.example.reservedassistance.Result;
import com.example.reservedassistance.Vo.OcrInformationVo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.ApiModel;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@ApiModel("ocr识别身份证信息")
@RequestMapping(value = "/ocr")
public class OcrController {


    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.ocr_api20210707.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.ocr_api20210707.Client(config);
    }


    @SneakyThrows
    @RequestMapping("ocr")
    public Result<OcrInformationVo> ocr(@RequestParam("file")MultipartFile file){
        com.aliyun.ocr_api20210707.Client client = OcrController.createClient("LTAI5t9kLGTJg7bYxBeAWxca", "0At6LQHUl25Lb6sjiToey81N4R19dk");
        // 需要安装额外的依赖库，直接点击下载完整工程即可看到所有依赖。
        java.io.InputStream bodySyream = file.getInputStream();
        com.aliyun.ocr_api20210707.models.RecognizeIdcardRequest recognizeIdcardRequest = new com.aliyun.ocr_api20210707.models.RecognizeIdcardRequest()
                .setBody(bodySyream);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            RecognizeIdcardResponse response = client.recognizeIdcardWithOptions(recognizeIdcardRequest, runtime);
            RecognizeIdcardResponseBody body = response.getBody();
            String data = body.getData();
            JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
            JsonObject information = jsonObject.getAsJsonObject("data").getAsJsonObject("face")
                    .getAsJsonObject("data");
            OcrInformationVo vo = new Gson().fromJson(information, OcrInformationVo.class);
            return Result.success(vo);
//            return idCardRoot;
        } catch (Exception error) {
            return Result.fail("识别失败，请确保上传的是带有人脸的一面");
        }
    }


}
