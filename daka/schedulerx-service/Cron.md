#####Cron是一款类Unix的操作系统下的基于时间的任务管理系统。您可以通过Cron在固定时间、日期间隔下运行定时任务（可以是命令和脚本）。
```
Cron表达式
Cron的表达式为：秒分时 日月周[年]
```

**为了帮助您理解，下面介绍一些常用的Cron表达式示例。**

|Contab表达式|	说明|
|  ----  | ----  |
|0 */1 * * * ?|	每隔1分钟触发一次|
|0 0 5-15 * * ?|	每天5:00~15:00整点触发|
|0 0/3 * * * ?|	每隔3分钟触发一次|
|0 0-5 14 * * ?|	每天14:00~14:05期间每隔1分钟触发一次|
|0 0/5 14 * * ?|	每天14:00~14:55期间每隔5分钟触发一次|
|0 0/5 14,18 * * ?|	每天14:00~14:55和18:00~18:55两个时间段内每5分钟触发一次|
|0 0/30 9-17 * * ?|	每天9:00~17:00内每半小时触发一次|
|0 0 10,14,16 * * ?|	每天10:00、14:00和16:00触发|
|0 0 12 ? * WED|	每周三12:00触发|
|0 0 17 ? * TUES,THUR,SAT|	每周二、周四、周六17:00触发|
|0 10,44 14 ? 3 WED	|每年3月的每周三的14:10和14:44触发|
|0 15 10 ? * MON-FRI|	周一至周五的上午10:15触发|
|0 0 23 L * ?|	每月最后一天23:00触发|
|0 15 10 L * ?|	每月最后一天10:15触发|
|0 15 10 ? * 6L|	每月最后一个周五10:15触发|
|0 15 10 * * ? 2005|	2005年的每天10:15触发|
|0 15 10 ? * 6L 2002-2005|	2002年~2005年的每月的最后一个周五上午10:15触发|
|0 15 10 ? * 6#3|	每月的第三个周五10:15触发|