-- 创建数据库并检查是否创建成功
drop database if exists redculture;
create database if not exists redculture;
show databases;

-- 切换数据库
use redculture;

-- 创建用户表
create table if not exists users
(
    user_id      int primary key auto_increment,
    username     varchar(20) not null,
    password     varchar(20) not null,
    role         int default 1,
    study_points int default 0
);
desc users;


-- 创建景点表
create table if not exists spots
(
    spot_id   int primary key auto_increment,
    spot_name varchar(20) not null,
    location  varchar(20) not null,
    history   text        not null
);
desc spots;

-- 学习记录表
-- drop table if exists records;
create table if not exists records
(
    record_id    int primary key auto_increment,
    user_id      int      not null,
    spot_id      int      not null,
    produce_time datetime not null,
    learn_note   text,
    foreign key (user_id) references users (user_id) on update cascade on delete cascade -- 外键
);
alter table records
    add constraint cons_spotId foreign key (spot_id) references spots (spot_id) on update cascade on delete cascade; -- 使用alter语句增加外键
desc records;

-- 创建公告表
create table if not exists announcements
(
    anno_id   int primary key auto_increment,
    title     varchar(50) not null,
    content   text        not null,
    post_time datetime    not null,
    comment   varchar(20)
);
desc announcements;

-- 插入用户数据
insert into users(username, password)
values ("张三", "zhangsan"),
       ("李四", "lisi"),
       ("王五", "wangwu"),
       ("赵六", "zhaoliu"),
       ("钱七", "qianqi"),
       ("孙八", "sunba"),
       ("周九", "zhoujiu"),
       ("吴十", "wushi");
select *
from users;

-- 插入管理员
insert into users(username, password, role)
values ("zhangsan", "123456", 0);

-- 插入景点数据
insert into spots(spot_name, location, history)
values ("中共一大会址纪念馆", "上海市黄浦区",
        "中国共产党第一次全国代表大会在此召开，标志着中国共产党的诞生，是中国革命红色基因的发源地。"),
       ("嘉兴南湖红船", "浙江省嘉兴",
        "中共一大最后一天的会议转移到南湖红船上举行，宣告了中国共产党的诞生，是中国革命的原点象征。"),
       ("井冈山革命根据地", "江西井冈山",
        "毛泽东、朱德在此创建第一个农村革命根据地，开辟“农村包围城市”的革命道路，被誉为“中国革命的摇篮”。"),
       ("遵义会议会址", "贵州省遵义市",
        "遵义会议确立了毛泽东的领导地位，是中国共产党历史上生死攸关的转折点，标志着党从幼稚走向成熟。"),
       ("延安革命纪念馆", "陕西省延安市",
        "延安是中国革命的圣地，中国共产党在此领导革命13年，孕育了延安精神，是抗日战争和解放战争的总后方。"),
       ("西柏坡纪念馆", "河北省石家庄市",
        "西柏坡是解放战争时期党中央所在地，“新中国从这里走来”，三大战役在此指挥，提出了“两个务必”的赶考精神。"),
       ("韶山毛泽东同志纪念馆", "湖南省湘潭市",
        "韶山是毛泽东的故乡，纪念馆展示了毛泽东的生平事迹和革命历程，是感受伟人成长足迹的重要场所。"),
       ("南京中山陵", "江苏省南京市",
        "中山陵是中国近代伟大的革命先驱孙中山的纪念地，体现了对革命先驱的缅怀和对未来的激励。"),
       ("南昌八一起义纪念馆", "江西省南昌市",
        "南昌起义打响武装反抗国民党反动派的第一枪，标志着中国共产党独立领导革命战争、创建人民军队的开端。"),
       ("百色起义纪念馆", "广西壮族自治区百色市",
        "百色起义是邓小平同志领导的起义，创建了右江革命根据地，是少数民族地区革命斗争的光辉典范。");
select *
from spots;

-- 插入学习记录
insert into records(user_id, spot_id, produce_time, learn_note)
values (2, 1, "2025/6/23", "好好学习，天天向上");
select *
from records;

-- 插入公告
insert into announcements(title, content, post_time)
values ("升级维护通知", "为提升系统性能、优化用户体验，并确保数据安全，[系统名称/平台名称]将于近期进行计划内升级维护。
届时系统将暂停服务，请您提前做好数据备份及业务安排。", "2025/6/23");
select *
from announcements;









