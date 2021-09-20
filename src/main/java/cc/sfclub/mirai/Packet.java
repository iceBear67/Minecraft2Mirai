package cc.sfclub.mirai;

import cc.sfclub.core.Core;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.SneakyThrows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class Packet {
    protected static final Gson gson = new Gson();
    protected static final Logger logger = LoggerFactory.getLogger("Packets");
    private volatile String rawResponse;
    private Result result;

    public Result asResult() {
        return result;
    }

    @SneakyThrows
    public HttpRequest buildRequest() {
        String packet = gson.toJson(this);
        if (debugPacketContent() && Core.get().config().isDebug())
            logger.info("[MiraiAdapter] new packet({}): {}", this.getClass().getSimpleName(), packet);
        var builder = HttpRequest.newBuilder(new URI(Config.getInst().baseUrl+getTargetedPath()));
        if(getMethod()==HttpMethod.GET){
            builder =builder.GET();
        }else{
            builder = builder.POST(HttpRequest.BodyPublishers.ofString(packet));
        }
        builder.version(HttpClient.Version.HTTP_1_1);
        return builder.build();
    }
    public boolean debugPacketContent(){
        return true;
    }
    @SneakyThrows
    public String getRawResponse(){ // spin lock waiting...
        int count=0;
        while(rawResponse==null){
            count++;
            if(count > Config.getInst().responseWaitTime){
                logger.warn("[MiraiAdapter] Waiting for response....failed!");
                break;
            }
            Thread.sleep(1000L);
        }
        return rawResponse;
    }
    @SneakyThrows
    public Packet sendSync(){
        var response=AdapterMain.getHttpClient().send(buildRequest(), HttpResponse.BodyHandlers.ofString());
        rawResponse=response.body();


            if (!rawResponse.startsWith("[")) {
                if (response.statusCode() != 200) {
                    logger.info("Mirai-API-Http returned an {}", response.statusCode());
                    logger.info("Response: ", response);
                    result = Result.HTTP_ERROR;
                    return this;
                }
                try {
                    result = gson.fromJson(rawResponse, Status.class).asResult();
                } catch (JsonSyntaxException e) {
                    logger.error("[MiraiAdapter] Packet {} occurs an error while parsing the json: {}", this.getClass().getSimpleName(), response);
                    logger.error("[MiraiAdapter] Request:", gson.toJson(this));
                }
                if (result != Result.SUCCESS) {
                    logger.warn("[MiraiAdapter] Packet {}' status has something wrong!(Code: {})", this.getClass().getSimpleName(), result);
                }
            }
            return this;
    }
    @SneakyThrows
    public Packet send(){
        AdapterMain.getHttpClient().sendAsync(buildRequest(), HttpResponse.BodyHandlers.ofString()).thenApply(response->{
            var trawResponse = response.body();
            rawResponse=trawResponse;
            if (!trawResponse.startsWith("[")) {
                if (response.statusCode() != 200) {
                    logger.info("Mirai-API-Http returned an {}", response.statusCode());
                    logger.info("Response: ", response);
                    result = Result.HTTP_ERROR;
                    return this;
                }
                try {
                    result = gson.fromJson(trawResponse, Status.class).asResult();
                } catch (JsonSyntaxException e) {
                    logger.error("[MiraiAdapter] Packet {} occurs an error while parsing the json: {}", this.getClass().getSimpleName(), response);
                    logger.error("[MiraiAdapter] Request:", gson.toJson(this));
                }
                if (result != Result.SUCCESS) {
                    logger.warn("[MiraiAdapter] Packet {}' status has something wrong!(Code: {})", this.getClass().getSimpleName(), result);
                }
            }
            return trawResponse;
        });
        return this;
    }

    public abstract String getTargetedPath();
    public abstract HttpMethod getMethod();
    public String getMediaType(){
        return HttpMediaType.APPLICATION_JSON;
    }
    public enum HttpMethod{
        GET,POST;
    }
    public class HttpMediaType{
        public static final String APPLICATION_JSON="application/json";
    }
    public enum Result {
        SUCCESS,
        WRONG_KEY,
        BOT_NOT_FOUND,
        INVALID_SESSION,
        UNVERIFIED_SESSION,
        MESSAGE_TARGET_NOT_FOUND,
        FILE_NOT_FOUND,
        // SPECIALS
        NO_PERMISSION,
        BOT_MUTED,
        MESSAGE_TOO_LONG,
        HTTP_ERROR,
        UNKNOWN;
    }

    public class Status{
        private int code=0;
        public Result asResult(){
            if(code+1>7){//Element which index bigger than 7 is special code..
                switch(code){
                    case 10:
                        return Result.NO_PERMISSION;
                    case 20:
                        return Result.BOT_MUTED;
                    case 30:
                        return Result.MESSAGE_TOO_LONG;
                    case 400:
                        return Result.UNKNOWN;
                    default:
                        logger.warn("[MiraiAdapter] INvaild status code: {}", code);
                        return Result.UNKNOWN;
                }
            }
            return Result.values()[code];
        }
    }
}
