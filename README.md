## NovelAPI
提供常见小说网站的便捷API

#### API支持

1. Sfacg轻小说

#### 关于jar包

所有jar包在release文件夹下（打包的jar包内包含了gson、jsoup依赖）

#### 下载API的使用

- 获取一个DownloadTask实例

```java
DownloadTask downloadTask = DownloadTasks.getDownloadTask(NovelSource.Sfacg);
```

- 配置下载参数DownloadParams

```java
DownloadParams params = new DownloadParams();
// 文件将保存在该目录下
params.parent = new File("E:\\Text File\\Novel");
// 文件名
params.fileName = "咸鱼少女拒绝翻身.txt";
// sfacg小说ID
params.novelId = 233718;
// 多线程并行请求章节内容,开启后下载速度较快，百万字小说只需4s
params.multiThreadOn = true;									
```

- 下载小说

```java
downloadTask.startDownload(params);			// 开始下载
boolean res = downloadTask.waitFinished();	// 等待下载任务结束
System.out.println("Download success: " + res);
System.out.println("Status message: " + downloadTask.getStatusMsg());
```

#### SfacgAPI的使用

1. 模拟登录

```java
SfacgAPI api = new SfacgAPI("username", "password");
api.login();
```

2. 获取当前账号详细信息

```java
AccountJson info = api.getAccountJson();
```

3. 获取章节列表信息

```java
ChapListJson chapListJson = api.getChapListJson(591785);
```

4. 获取章节内容（文字，不支持vip章节）

```java
ChapContentJson chapContentJson = api.getChapContentJson(6742076);
```

5. 保存vip章节（图片）

```java
boolean saveRes = api.saveVipChapPic(novelId, chapId, file);
```

该函数只能保存图片形式的vip章节，而且图片非常模糊

#### 其他文档

其他文档在doc文件夹下
