-- auto-generated definition
create table user
(
    id           bigint auto_increment comment 'id
'
        primary key,
    username     varchar(256)                        null comment '用户昵称',
    userAccount  varchar(256)                        null comment '账户',
    avatarUrl    varchar(1024)                       null comment '用户头像',
    gender       tinyint                             null comment '性别',
    userPassword varchar(512)                        null comment '密码',
    phone        varchar(128)                        null comment '电话',
    userStatus   int       default 0                 not null comment '状态 0 - 正常',
    email        varchar(512)                        null comment '邮箱',
    createTime   datetime  default CURRENT_TIMESTAMP null comment '创建时间',
    isDelete     tinyint   default 0                 not null comment '是否删除',
    updateTime   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    userRole     int       default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512)                        null comment '星球编号'
)
    comment '用户';

