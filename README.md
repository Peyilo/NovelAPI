# NovelAPI
提供常见小说网站的便捷API

### API支持

1. Sfacg轻小说

### SfacgAPI使用

#### 下载功能

- 获取一个DownloadTask实例

```java
DownloadTask downloadTask = DownloadTasks.getDownloadTask(SourceIdentifier.Sfacg);
```

- 配置下载参数DownloadParams实例

```java
DownloadParams params = new DownloadParams();
params.parent = new File("E:\\Text File\\Novel");			// 文件将保存在该目录下
params.novelId = 233718;									// sfacg小说ID
params.fileName = "咸鱼少女拒绝翻身.txt";					   // 文件名
params.threadOn = true;										// 多线程并行请求章节内容
```

- 开始下载

```java
downloadTask.startDownload(params);						// 开始下载
while (!downloadTask.isFinish()) {						// 等待下载任务结束
}
```

