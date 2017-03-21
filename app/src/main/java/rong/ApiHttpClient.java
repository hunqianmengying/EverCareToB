package rong;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.List;

import rong.models.FormatType;
import rong.models.SdkHttpResult;
import rong.utils.HttpUtil;

public class ApiHttpClient {

    private static final String RONGCLOUDURI = "http://api.cn.ronghub.com";

    private static final String UTF8 = "UTF-8";

    // 获取token
    public static SdkHttpResult getToken(String appKey, String appSecret,
                                         String userId, String userName, String portraitUri,
                                         FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey, appSecret, RONGCLOUDURI + "/user/getToken." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        sb.append("&name=").append(URLEncoder.encode(userName == null ? "" : userName, UTF8));
        sb.append("&portraitUri=").append(URLEncoder.encode(portraitUri == null ? "" : portraitUri, UTF8));
        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 检查用户在线状态
    public static SdkHttpResult checkOnline(String appKey, String appSecret,
                                            String userId, FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret,
                RONGCLOUDURI + "/user/checkOnline." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 刷新用户信息
    public static SdkHttpResult refreshUser(String appKey, String appSecret,
                                            String userId, String userName, String portraitUri,
                                            FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret, RONGCLOUDURI + "/user/refresh." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        if (userName != null) {
            sb.append("&name=").append(URLEncoder.encode(userName, UTF8));
        }
        if (portraitUri != null) {
            sb.append("&portraitUri=").append(
                    URLEncoder.encode(portraitUri, UTF8));
        }

        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 封禁用户
    public static SdkHttpResult blockUser(String appKey, String appSecret,
                                          String userId, int minute, FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret, RONGCLOUDURI + "/user/block." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        sb.append("&minute=").append(
                URLEncoder.encode(String.valueOf(minute), UTF8));

        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 解禁用户
    public static SdkHttpResult unblockUser(String appKey, String appSecret,
                                            String userId, FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret, RONGCLOUDURI + "/user/unblock." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));

        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 获取被封禁用户
    public static SdkHttpResult queryBlockUsers(String appKey,
                                                String appSecret, FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret,
                RONGCLOUDURI + "/user/block/query." + format.toString());

        return HttpUtil.returnResult(conn);
    }

    // 添加用户到黑名单
    public static SdkHttpResult blackUser(String appKey, String appSecret,
                                          String userId, List<String> blackUserIds, FormatType format)
            throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret,
                RONGCLOUDURI + "/user/blacklist/add." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        if (blackUserIds != null) {
            for (String blackId : blackUserIds) {
                sb.append("&blackUserId=").append(
                        URLEncoder.encode(blackId, UTF8));
            }
        }

        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 从黑名单移除用户
    public static SdkHttpResult unblackUser(String appKey, String appSecret,
                                            String userId, List<String> blackUserIds, FormatType format)
            throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret,
                RONGCLOUDURI + "/user/blacklist/remove." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        if (blackUserIds != null) {
            for (String blackId : blackUserIds) {
                sb.append("&blackUserId=").append(
                        URLEncoder.encode(blackId, UTF8));
            }
        }

        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 获取黑名单用户
    public static SdkHttpResult QueryblackUser(String appKey, String appSecret,
                                               String userId, FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret,
                RONGCLOUDURI + "/user/blacklist/query." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));

        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 创建群
    public static SdkHttpResult createGroup(String appKey, String appSecret,
                                            List<String> userIds, String groupId, String groupName,
                                            FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret, RONGCLOUDURI + "/group/create." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("groupId=").append(URLEncoder.encode(groupId, UTF8));
        sb.append("&groupName=").append(URLEncoder.encode(groupName == null ? "" : groupName, UTF8));
        if (userIds != null) {
            for (String id : userIds) {
                sb.append("&userId=").append(URLEncoder.encode(id, UTF8));
            }
        }
        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 加入群
    public static SdkHttpResult joinGroup(String appKey, String appSecret,
                                          String userId, String groupId, String groupName, FormatType format)
            throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret, RONGCLOUDURI + "/group/join." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        sb.append("&groupId=").append(URLEncoder.encode(groupId, UTF8));
        sb.append("&groupName=").append(URLEncoder.encode(groupName == null ? "" : groupName, UTF8));
        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 批量加入群
    public static SdkHttpResult joinGroupBatch(String appKey, String appSecret,
                                               List<String> userIds, String groupId, String groupName,
                                               FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret, RONGCLOUDURI + "/group/join." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("groupId=").append(URLEncoder.encode(groupId, UTF8));
        sb.append("&groupName=").append(URLEncoder.encode(groupName == null ? "" : groupName, UTF8));
        if (userIds != null) {
            for (String id : userIds) {
                sb.append("&userId=").append(URLEncoder.encode(id, UTF8));
            }
        }
        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 退出群
    public static SdkHttpResult quitGroup(String appKey, String appSecret,
                                          String userId, String groupId, FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret, RONGCLOUDURI + "/group/quit." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        sb.append("&groupId=").append(URLEncoder.encode(groupId, UTF8));
        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 批量退出群
    public static SdkHttpResult quitGroupBatch(String appKey, String appSecret,
                                               List<String> userIds, String groupId, FormatType format)
            throws Exception {

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(appKey,
                appSecret, RONGCLOUDURI + "/group/quit." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("groupId=").append(URLEncoder.encode(groupId, UTF8));
        if (userIds != null) {
            for (String id : userIds) {
                sb.append("&userId=").append(URLEncoder.encode(id, UTF8));
            }
        }

        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

    // 解散群
    public static SdkHttpResult dismissGroup(String appKey, String appSecret,
                                             String userId, String groupId, FormatType format) throws Exception {

        HttpURLConnection conn = HttpUtil
                .CreatePostHttpConnection(appKey, appSecret, RONGCLOUDURI
                        + "/group/dismiss." + format.toString());

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoder.encode(userId, UTF8));
        sb.append("&groupId=").append(URLEncoder.encode(groupId, UTF8));
        HttpUtil.setBodyParameter(sb, conn);

        return HttpUtil.returnResult(conn);
    }

}
