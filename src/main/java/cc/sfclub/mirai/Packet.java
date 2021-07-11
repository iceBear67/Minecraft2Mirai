package cc.sfclub.mirai;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Packet {
    protected static final Gson gson = new Gson();
    protected static final Logger logger = LoggerFactory.getLogger("Packets");
    @Getter
    private String rawResponse;
    private Result result;

    public Result asResult() {
        return result;
    }

    public Request buildRequest() {
        String packet = gson.toJson(this);
        if (debugPacketContent() && AdapterMain.getPlugin(AdapterMain.class).getMainConfig().debug)
            logger.info("[MiraiAdapter] new packet({}): {}", this.getClass().getSimpleName(), packet);
        Request.Builder builder=new Request.Builder().url(Config.getInst().baseUrl+getTargetedPath());
        if(getMethod()==HttpMethod.GET){
            builder.get();
        }else{
            builder.post(RequestBody.create(packet, MediaType.parse(getMediaType())));
        }
        return builder.build();
    }
    public boolean debugPacketContent(){
        return true;
    }
    @SneakyThrows
    public Packet send(){
        Response response = AdapterMain.getHttpClient().newCall(buildRequest()).execute();
        rawResponse = response.body().string();
        if (!rawResponse.startsWith("[")) {
            if (response.code() != 200) {
                logger.info("Mirai-API-Http return an {}", response.code());
                logger.info("Response: ", response);
                result = Result.HTTP_ERROR;
                return this;
            }
            try {
                result = gson.fromJson(rawResponse, Status.class).asResult();
            } catch (JsonSyntaxException e) {
                logger.error("Packet {} occurs an error while parsing the json: {}", this.getClass().getSimpleName(), response);
                logger.error("Request:", gson.toJson(this));
            }
            if (result != Result.SUCCESS) {
                logger.warn("Packet {}' status has something wrong!(Code: {})", this.getClass().getSimpleName(), result);
            }
        }
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
