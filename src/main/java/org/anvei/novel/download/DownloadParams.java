package org.anvei.novel.download;

import org.anvei.novel.api.API;

import java.io.File;

public class DownloadParams {

    public File parent;             // 保存在该文件夹下

    public long novelId;            // 小说id，对于一个网站来说，每一本小说都应该有一个唯一id标识

    public String fileName;         // 文件名称

    public long timeout = -1;       // 超时时间

    public int maxThreadCount = 1;

    public String account;          // 账号

    public String certificate;      // 登录凭证

    public API api;                 // 如果配置了该属性，account、certificate会被忽略

}
