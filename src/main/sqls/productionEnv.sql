drop database if exists redCulture;
create database if not exists redCulture
    character set utf8mb4 collate utf8mb4_unicode_ci;

use redCulture;

# 用户表。
# 它的ID需要特殊操作，使其能够格式化。
# 为了从ID信息上就能看出来用户的身份，
# 规定以下格式：
# GXXX与AXX两种。
# 其中，X表示数值0 - 9，每一个X表示一个数值。
create table if not exists users
(
    user_id            varchar(4) primary key comment "用户ID（格式: G001，A01。其中，数字的长度要严格按示例限定）",
    username           varchar(50)  not null comment "用户名",
    password           varchar(255) not null comment "密码",
    role               int      default 1 comment "角色。值为1是普通用户，值为0是管理员",
    points             int      default 0 comment "学习积分",
    create_time        datetime default current_timestamp comment "账号创建的时间",
    last_accessed_time datetime default null comment "上次登录时间"
);

# 为了实现格式化user_id的触发器，要先设计一张表，作为它的计数器
create table if not exists user_id_counter
(
    counter_name char(9) primary key comment "计数器名称。用 user_G 和 user_A 分别表示。G是普通用户，A是管理员。",
    last_number  int not null default 0 comment "上一次操作后计数器的编号"
);

# 初始化计数器
insert ignore into user_id_counter(counter_name)
values ('user_G'),
       ('user_A');

#<editor-fold desc = "在插入数据前，格式化user_id">
delimiter $$
create trigger tg_autoincrement_user_id
    before insert
    on users
    for each row
begin
    declare id_prefix char(1);
    declare counter_key char(9);
    declare id_limit int; # 储存用户数量的上限
    declare next_number int;
    declare formatted_num varchar(3);
    # 存储填充好的数值字符。
    #<editor-fold desc="根据role字段判断字符串首">
    if NEW.role = 0 then
        set id_prefix = 'A';
        set counter_key = 'user_A';
        set id_limit = 99;
    else # 目前假定为普通用户。
        set id_prefix = 'G';
        set counter_key = 'user_G';
        set id_limit = 999;
    end if;
    #</editor-fold>

    #<editor-fold desc="获取将要注册用户的ID数值。">
    # 如果注册失败，自动回滚。
    update user_id_counter
    set last_number = last_number + 1
    where counter_name = counter_key;

    select last_number
    into next_number
    from user_id_counter
    where counter_name = counter_key;

    if next_number > id_limit then
        signal sqlstate '80002' set message_text = '用户数量已经达到了上限，没有注册新用户的空间了。';
    end if;
    #</editor-fold>

    #<editor-fold desc = "填充数值字符">
    if id_prefix = 'A' then
        set formatted_num = lpad(next_number, 2, '0');
    else
        set formatted_num = lpad(next_number, 3, '0');
    end if;
    #</editor-fold>

    # 更新最终的字段
    set NEW.user_id = concat(id_prefix, formatted_num);

end $$
delimiter ;
#</editor-fold>

create table if not exists spots
(
    id       int primary key auto_increment comment "景点ID",
    name     varchar(50)  not null comment "景点名称",
    location varchar(100) not null comment "景点位置",
    history  text         not null comment "景点历史描述"
);

create table if not exists notes
(
    id           int primary key auto_increment comment "记录ID",
    user_id      varchar(4) not null comment "与用户表的用户ID关联",
    spot_id      int        not null comment "与景点表的景点ID关联",
    produce_time datetime   not null comment "学习记录产生时间",
    note         text comment "学习笔记",
    constraint fk_notes_uid_user foreign key (user_id) REFERENCES users (user_id),
    constraint fk_notes_sid_spots foreign key (spot_id) REFERENCES spots (id)
);

#<editor-fold desc = "禁止为管理员用户插入学习记录">
delimiter $$
create trigger tg_check_user_type
    before insert
    on notes
    for each row
begin
    if left(NEW.user_id, 1) = 'A' then
        signal sqlstate '80003' set message_text = '不能为管理员用户插入学习记录。';
    end if;
end $$

delimiter ;
#</editor-fold>

create table if not exists records
(
    record_id    int primary key auto_increment,
    user_id      varchar(4) not null,
    spot_id      int        not null,
    produce_time datetime   not null,
    learn_note   text,
    foreign key (user_id) references users (user_id) on
        update
        cascade on
        delete
        cascade
    -- 外键
);
alter table records
    add constraint cons_spotId foreign key (spot_id) references spots (id) on update cascade on delete cascade; -- 使用alter语句增加外键
desc records;

create table if not exists announcements
(
    id        int primary key auto_increment comment "公告ID",
    title     varchar(100) not null comment "公告标题",
    content   text         not null comment "公告内容",
    post_time datetime     not null comment "发布时间",
    remarks   varchar(255) comment "备注信息"
);

-- 插入用户数据
insert into users(username, password)
values ('张三', 'zhangsan'),
       ('李四', 'lisi'),
       ('王五', 'wangwu'),
       ('赵六', 'zhaoliu'),
       ('钱七', 'qianqi'),
       ('孙八', 'sunba'),
       ('周九', 'zhoujiu'),
       ('吴十', 'wushi');

insert into users(username, password, role)
values ('admin', 'test123456', 0);

-- 插入景点数据
INSERT INTO spots (name, location, history)
VALUES ('中共一大会址纪念馆', '上海市黄浦区',
        '中国共产党第一次全国代表大会在此召开，标志着中国共产党的诞生，是中国革命红色基因的发源地。'),
       ('嘉兴南湖红船', '浙江省嘉兴',
        '中共一大最后一天的会议转移到南湖红船上举行，宣告了中国共产党的诞生，是中国革命的原点象征。'),
       ('井冈山革命根据地', '江西井冈山',
        '毛泽东、朱德在此创建第一个农村革命根据地，开辟“农村包围城市”的革命道路，被誉为“中国革命的摇篮”。'),
       ('遵义会议会址', '贵州省遵义市',
        '遵义会议确立了毛泽东的领导地位，是中国共产党历史上生死攸关的转折点，标志着党从幼稚走向成熟。'),
       ('延安革命纪念馆', '陕西省延安市',
        '延安是中国革命的圣地，中国共产党在此领导革命13年，孕育了延安精神，是抗日战争和解放战争的总后方。'),
       ('西柏坡纪念馆', '河北省石家庄市',
        '西柏坡是解放战争时期党中央所在地，“新中国从这里走来”，三大战役在此指挥，提出了“两个务必”的赶考精神。'),
       ('韶山毛泽东同志纪念馆', '湖南省湘潭市',
        '韶山是毛泽东的故乡，纪念馆展示了毛泽东的生平事迹和革命历程，是感受伟人成长足迹的重要场所。'),
       ('南京中山陵', '江苏省南京市',
        '中山陵是中国近代伟大的革命先驱孙中山的纪念地，体现了对革命先驱的缅怀和对未来的激励。'),
       ('南昌八一起义纪念馆', '江西省南昌市',
        '南昌起义打响武装反抗国民党反动派的第一枪，标志着中国共产党独立领导革命战争、创建人民军队的开端。'),
       ('百色起义纪念馆', '广西壮族自治区百色市',
        '百色起义是邓小平同志领导的起义，创建了右江革命根据地，是少数民族地区革命斗争的光辉典范。');

-- 插入学习笔记
# 如果是管理员用户，就不能插入，需要阻止。由触发器进行检查。
INSERT INTO notes (user_id, spot_id, produce_time, note)
VALUES ('G001', 1, '2025-08-26 17:57:00', '好好学习，天天向上');

-- 插入学习记录
insert into records(user_id, spot_id, produce_time, learn_note)
values ('G001', 1, '2025-08-26 20:13:00', '好好学习，天天向上');

-- 插入公告
INSERT INTO announcements (title, content, post_time)
VALUES ('升级维护通知', '为提升系统性能、优化用户体验，并确保数据安全，
[系统名称/平台名称]将于近期进行计划内升级维护。届时系统将暂停服务，请您提前做好数据备份及业务安排。',
        '2025-06-23 00:00:00');