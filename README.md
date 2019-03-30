# group-chat
基于java的局域网内群聊系统

####客户端架构
发送消息
post:send [int:size] [message]\n

发送获取所有客户端请求
post:alluser\n

发送主动心跳
heart:client initiative\n

发送被动心跳
heart:client passive\n



####服务器架构
##广播
新用户广播
broadcast:new_client [int:size] [String:ip+port]\n

用户退出广播
broadcast:end_client [int:size] [String:ip+port]\n

消息广播
broadcast:message [int:size] [ip+port+message]\n

##交互
消息已接受
server:getsend [int:message_hash]\n
传回所有客户端
server:alluser [int:size] [int:len] [String:ip+port] [String:ip+port]\n
//size为后面数据的大小(包含len之后的空格)
//len为用户数量

##心跳
发送主动心跳
heart:server initiative\n

发送被动心跳
heart:server passive\n



心跳相关
当一端 *持续* *未* 收到另一端的数据时 主动发出心跳请求 2秒内未回应或Socket被关闭则认为连接已结束
主动发送 回应被动
