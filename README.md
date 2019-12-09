# gmall
微服务商城
zookeeper + dubbo + tkmybatis + redis + es + activeMQ + vue
- 商城的文件存储用fastDFS
- 单点登录
- 实现了新浪微博社交登录，
- 支付宝支付接口调用
- redis以及redisson做分布式锁
- activeMQ做补偿性分布式事务
- 前端vue + JavaScript + Thymeleaf + Jquery
#端口划分
gmall-user-service      8070
gmall-user-web          8080

gmall-manage-service    8071
gmall-manage-web        8081

gmall-item-web          8082

gmall-redission-test    8171

gmall-search-service    8072
gmall-search-web        8083

gmall-cart-service      8073
gmall-cart-web          8084

gmall-user-service      8070
gmall-passport-web      8085

gmall-order-service     8074
gmall-order-web         8086

gmall-payment           8087

gmall-seckill           8001
