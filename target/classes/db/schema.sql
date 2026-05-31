CREATE DATABASE IF NOT EXISTS examination_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE examination_system;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `role` VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '角色：student/teacher/admin',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 初始化用户数据（密码均为明文，生产环境请加密）
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `email`, `phone`, `status`) VALUES
('admin', 'admin123', '系统管理员', 'admin', 'admin@example.com', '13800000001', 1),
('teacher', 'teacher123', '张老师', 'teacher', 'teacher@example.com', '13800000002', 1),
('teacher2', 'teacher123', '王老师', 'teacher', 'teacher2@example.com', '13800000004', 1),
('student', 'student123', '李同学', 'student', 'student@example.com', '13800000003', 1),
('student2', 'student123', '王同学', 'student', 'student2@example.com', '13800000005', 1),
('student3', 'student123', '赵同学', 'student', 'student3@example.com', '13800000006', 1);

-- ============================================================
-- 2. question - 题目表
-- ============================================================
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `type` VARCHAR(20) NOT NULL COMMENT '题目类型: single/multiple/true_false',
    `content` TEXT NOT NULL COMMENT '题干内容',
    `options` JSON NOT NULL COMMENT '选项: {"A":"...","B":"...","C":"...","D":"..."}',
    `answer` VARCHAR(50) NOT NULL COMMENT '正确答案: single/true_false单值; multiple逗号分隔如"A,B"',
    `analysis` TEXT DEFAULT NULL COMMENT '题目解析',
    `difficulty` TINYINT NOT NULL DEFAULT 1 COMMENT '难度: 1-简单 2-中等 3-困难',
    `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签,逗号分隔',
    `create_by` BIGINT NOT NULL COMMENT '出题教师ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_difficulty` (`difficulty`),
    INDEX `idx_create_by` (`create_by`),
    INDEX `idx_type_difficulty` (`type`, `difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- ============================================================
-- 3. exam - 考试表
-- ============================================================
CREATE TABLE IF NOT EXISTS `exam` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '考试ID',
    `title` VARCHAR(200) NOT NULL COMMENT '考试标题',
    `description` TEXT DEFAULT NULL COMMENT '考试描述',
    `start_time` DATETIME NOT NULL COMMENT '考试开始时间',
    `end_time` DATETIME NOT NULL COMMENT '考试结束时间',
    `duration_minutes` INT NOT NULL COMMENT '考试时长(分钟)',
    `total_score` INT NOT NULL DEFAULT 0 COMMENT '总分',
    `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '状态: draft/published/closed',
    `create_by` BIGINT NOT NULL COMMENT '创建教师ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_by` (`create_by`),
    INDEX `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- ============================================================
-- 4. exam_rule - 组卷规则表
-- ============================================================
CREATE TABLE IF NOT EXISTS `exam_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `question_type` VARCHAR(20) NOT NULL COMMENT '题目类型: single/multiple/true_false',
    `difficulty` TINYINT DEFAULT NULL COMMENT '难度: 1-简单 2-中等 3-中等, NULL表示不限',
    `count` INT NOT NULL COMMENT '题目数量',
    `score_per_question` INT NOT NULL COMMENT '每题分值',
    PRIMARY KEY (`id`),
    INDEX `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组卷规则表';

-- ============================================================
-- 5. exam_question - 考试题目关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS `exam_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `score` INT NOT NULL COMMENT '该题分值',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '题目顺序',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_exam_question` (`exam_id`, `question_id`),
    INDEX `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试题目关联表';

-- ============================================================
-- 6. exam_record - 考试记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS `exam_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `submit_time` DATETIME DEFAULT NULL COMMENT '提交时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'in_progress' COMMENT '状态: in_progress/submitted/graded',
    `total_score` INT DEFAULT NULL COMMENT '得分',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_exam_student` (`exam_id`, `student_id`),
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- ============================================================
-- 7. exam_answer - 学生答题记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS `exam_answer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '答题ID',
    `record_id` BIGINT NOT NULL COMMENT '考试记录ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `student_answer` VARCHAR(50) NOT NULL COMMENT '学生答案',
    `is_correct` TINYINT DEFAULT NULL COMMENT '是否正确: 0-错误 1-正确',
    `score` INT DEFAULT NULL COMMENT '得分',
    PRIMARY KEY (`id`),
    INDEX `idx_record_id` (`record_id`),
    INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生答题记录表';

-- ============================================================
-- 初始化题目数据（create_by=2 为张老师, create_by=3 为王老师）
-- ============================================================

-- ========== 单选题 (20道) ==========
INSERT INTO `question` (`type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `tags`, `create_by`) VALUES
-- Java 基础 (难度1)
('single', 'Java 中以下哪个关键字用于定义类？', '{"A":"class","B":"function","C":"def","D":"struct"}', 'A', 'Java 使用 class 关键字定义类', 1, 'Java基础', 2),
('single', '以下哪个不是 Java 的基本数据类型？', '{"A":"int","B":"boolean","C":"String","D":"double"}', 'C', 'String 是引用类型，不是基本数据类型', 1, 'Java基础', 2),
('single', 'Java 中 int 类型占用几个字节？', '{"A":"1","B":"2","C":"4","D":"8"}', 'C', 'int 类型在 Java 中占 4 字节（32位）', 1, 'Java基础', 2),
('single', '以下哪个集合类是线程安全的？', '{"A":"ArrayList","B":"HashMap","C":"Vector","D":"LinkedList"}', 'C', 'Vector 是线程安全的，ArrayList 不是', 1, 'Java集合', 2),
('single', 'Java 中 final 关键字不能修饰什么？', '{"A":"类","B":"方法","C":"变量","D":"包"}', 'D', 'final 可以修饰类、方法、变量，不能修饰包', 1, 'Java基础', 2),
-- Java 进阶 (难度2)
('single', '以下哪个不是 Java 的集合接口？', '{"A":"List","B":"Set","C":"Map","D":"Array"}', 'D', 'Array 是数组，不是集合接口', 2, 'Java集合', 2),
('single', 'MyBatis-Plus 中用于分页的插件是？', '{"A":"PaginationInterceptor","B":"PagePlugin","C":"PageHelper","D":"MyBatisPage"}', 'A', 'MyBatis-Plus 使用 PaginationInterceptor 实现分页', 2, 'MyBatis-Plus', 2),
('single', 'Spring 中 @Autowired 默认按什么注入？', '{"A":"类型","B":"名称","C":"构造参数","D":"随机"}', 'A', '@Autowired 默认按类型（byType）注入', 2, 'Spring', 2),
('single', 'Java 中 equals() 和 == 的区别，以下说法正确的是？', '{"A":"没有区别","B":"equals比较值，==比较引用","C":"equals比较引用，==比较值","D":"都用于比较引用"}', 'B', 'equals 比较对象内容，== 比较引用地址', 2, 'Java基础', 2),
('single', '以下哪个不是 SpringBoot 的自动配置类？', '{"A":"DataSourceAutoConfiguration","B":"WebMvcAutoConfiguration","C":"UserAutoConfiguration","D":"RedisAutoConfiguration"}', 'C', 'SpringBoot 没有 UserAutoConfiguration', 2, 'SpringBoot', 2),
-- 进阶/困难 (难度3)
('single', 'JVM 中哪个内存区域存放对象实例？', '{"A":"方法区","B":"堆","C":"栈","D":"程序计数器"}', 'B', '堆内存用于存放所有对象实例', 3, 'JVM', 2),
('single', 'Spring AOP 默认使用哪种代理方式？', '{"A":"CGLIB","B":"JDK 动态代理","C":"字节码生成","D":"静态代理"}', 'B', 'Spring AOP 默认使用 JDK 动态代理，目标类没有接口时使用 CGLIB', 3, 'Spring', 2),
('single', 'MySQL 中 InnoDB 的默认隔离级别是？', '{"A":"读未提交","B":"读已提交","C":"可重复读","D":"串行化"}', 'C', 'InnoDB 默认隔离级别为可重复读（REPEATABLE READ）', 3, '数据库', 2),
('single', 'Redis 的默认数据结构底层实现是什么？', '{"A":"B+树","B":"跳表","C":"哈希表+链表","D":"二叉树"}', 'C', 'Redis 字典使用哈希表，冲突时使用链表', 3, '数据库', 3),
('single', 'HTTP/2 相比 HTTP/1.1 最大的改进是？', '{"A":"支持 JSON","B":"多路复用","C":"支持 POST","D":"支持 Cookie"}', 'B', 'HTTP/2 引入了多路复用，解决了队头阻塞问题', 3, '网络', 2),
-- 网络/通用
('single', 'Spring Boot 默认内嵌的 Web 服务器是？', '{"A":"Tomcat","B":"Jetty","C":"Undertow","D":"Nginx"}', 'A', 'Spring Boot 默认使用 Tomcat', 1, 'SpringBoot', 2),
('single', 'HTTP 状态码 404 表示什么？', '{"A":"服务器错误","B":"未授权","C":"资源未找到","D":"请求超时"}', 'C', '404 表示请求的资源不存在', 1, '网络', 2),
('single', 'RESTful API 中，以下哪个 HTTP 方法用于创建资源？', '{"A":"GET","B":"POST","C":"PUT","D":"DELETE"}', 'B', 'POST 用于创建资源，PUT 用于更新资源', 1, '网络', 2),
('single', '以下哪个注解用于标注 RESTful 接口？', '{"A":"@Controller","B":"@Service","C":"@RestController","D":"@Component"}', 'C', '@RestController 标注 RESTful 控制器', 1, 'SpringBoot', 2),
('single', 'Java 中以下哪个不是异常类型？', '{"A":"Error","B":"Exception","C":"RuntimeException","D":"Warning"}', 'D', 'Java 中没有 Warning 异常类型', 2, 'Java基础', 2);

-- ========== 多选题 (10道) ==========
INSERT INTO `question` (`type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `tags`, `create_by`) VALUES
('multiple', '以下哪些是 Java 的访问修饰符？', '{"A":"public","B":"private","C":"protected","D":"static"}', 'A,B,C', 'static 不是访问修饰符', 1, 'Java基础', 2),
('multiple', 'Spring 的核心特性包括？', '{"A":"IOC","B":"AOP","C":"DI","D":"MVC"}', 'A,B,C,D', 'Spring 框架包含 IOC、AOP、DI、MVC 等核心特性', 2, 'Spring', 2),
('multiple', '以下哪些是 HTTP 请求方法？', '{"A":"GET","B":"POST","C":"PUT","D":"DELETE"}', 'A,B,C,D', '常用的 HTTP 请求方法包括 GET、POST、PUT、DELETE', 1, '网络', 2),
('multiple', '以下哪些是 Java 的集合类？', '{"A":"ArrayList","B":"HashSet","C":"HashMap","D":"StringBuffer"}', 'A,B,C', 'StringBuffer 是字符串缓冲区，不是集合类', 1, 'Java集合', 2),
('multiple', 'SpringBoot 自动配置的优势包括？', '{"A":"减少配置","B":"快速开发","C":"自动依赖管理","D":"零代码编写"}', 'A,B,C', 'SpringBoot 不能完全消除代码编写', 2, 'SpringBoot', 2),
('multiple', '以下哪些是 MySQL 的存储引擎？', '{"A":"InnoDB","B":"MyISAM","C":"Memory","D":"Redis"}', 'A,B,C', 'Redis 不是 MySQL 存储引擎', 2, '数据库', 2),
('multiple', '以下哪些属于 HTTP 状态码分类？', '{"A":"1xx 信息","B":"2xx 成功","C":"3xx 重定向","D":"4xx 客户端错误"}', 'A,B,C,D', 'HTTP 状态码分为 1xx~5xx 五类', 1, '网络', 2),
('multiple', 'Java 中创建线程的方式有哪些？', '{"A":"继承 Thread 类","B":"实现 Runnable 接口","C":"实现 Callable 接口","D":"使用线程池"}', 'A,B,C,D', '四种都是创建线程或执行线程任务的方式', 3, 'Java并发', 2),
('multiple', 'MyBatis-Plus 提供的通用 Mapper 方法有哪些？', '{"A":"insert","B":"selectById","C":"updateById","D":"deleteById"}', 'A,B,C,D', 'BaseMapper 提供了这些通用 CRUD 方法', 2, 'MyBatis-Plus', 3),
('multiple', '以下哪些是常用的数据库索引类型？', '{"A":"B+树索引","B":"哈希索引","C":"全文索引","D":"位图索引"}', 'A,B,C,D', '这些都是常用的数据库索引类型', 3, '数据库', 2);

-- ========== 判断题 (10道) ==========
INSERT INTO `question` (`type`, `content`, `options`, `answer`, `analysis`, `difficulty`, `tags`, `create_by`) VALUES
('true_false', 'Java 中的接口可以多继承。', '{"A":"正确","B":"错误"}', 'A', 'Java 中一个接口可以继承多个接口', 2, 'Java基础', 2),
('true_false', 'Spring Boot 项目必须配置 application.yml 文件才能运行。', '{"A":"正确","B":"错误"}', 'B', 'Spring Boot 有默认配置，不配置 application.yml 也可以运行', 1, 'SpringBoot', 2),
('true_false', 'MySQL 的 InnoDB 引擎支持事务。', '{"A":"正确","B":"错误"}', 'A', 'InnoDB 是 MySQL 默认的事务型存储引擎', 1, '数据库', 2),
('true_false', 'Java 中的 String 类可以被继承。', '{"A":"正确","B":"错误"}', 'B', 'String 类是 final 的，不能被继承', 1, 'Java基础', 2),
('true_false', 'Spring 的 IOC 容器负责管理对象的生命周期。', '{"A":"正确","B":"错误"}', 'A', 'ioc 容器创建、配置、组装和管理 Bean 的生命周期', 2, 'Spring', 2),
('true_false', 'HashMap 允许 null 作为键和值。', '{"A":"正确","B":"错误"}', 'A', 'HashMap 允许一个 null 键和多个 null 值', 2, 'Java集合', 3),
('true_false', 'HTTP 协议是无状态的。', '{"A":"正确","B":"错误"}', 'A', 'HTTP 本身是无状态的，通过 Cookie/Session 实现状态管理', 1, '网络', 2),
('true_false', 'MySQL 的 DELETE 语句可以回滚。', '{"A":"正确","B":"错误"}', 'A', '在事务中使用 DELETE，未提交前可以回滚', 2, '数据库', 3),
('true_false', 'SpringBoot 只能通过 Maven 构建。', '{"A":"正确","B":"错误"}', 'B', 'SpringBoot 也支持 Gradle 构建', 1, 'SpringBoot', 3),
('true_false', 'RESTful API 应该使用 JSON 作为数据格式。', '{"A":"正确","B":"错误"}', 'A', '虽然 RESTful 不限定格式，但 JSON 是最常用的数据格式', 1, '网络', 2);

-- ============================================================
-- 初始化考试数据
-- ============================================================
INSERT INTO `exam` (`id`, `title`, `description`, `start_time`, `end_time`, `duration_minutes`, `total_score`, `status`, `create_by`, `create_time`, `update_time`) VALUES
(1, 'Java 基础知识测试', '考察 Java 基础概念、数据类型、面向对象等知识点',
 NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 60, 80, 'published', 2, NOW(), NOW()),
(2, 'SpringBoot 入门测试', '考察 SpringBoot 基础配置和核心特性',
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 3 DAY), 45, 50, 'published', 2, NOW(), NOW()),
(3, '计算机网络综合测试', '考察 HTTP 协议、RESTful API 设计等网络知识',
 NOW(), DATE_ADD(NOW(), INTERVAL 5 DAY), 30, 60, 'published', 3, NOW(), NOW()),
(4, '数据库原理与 SQL', '考察 MySQL 基础、索引、事务等知识点',
 NOW(), DATE_ADD(NOW(), INTERVAL 10 DAY), 90, 110, 'published', 3, NOW(), NOW()),
(5, '综合编程能力测试', 'Java + Spring + 数据库综合考察',
 DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), 120, 0, 'draft', 2, NOW(), NOW()),
(6, 'JVM 与并发编程', '深入考察 JVM 内存模型、垃圾回收、多线程并发',
 DATE_ADD(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 12 DAY), 60, 0, 'draft', 2, NOW(), NOW());

-- ============================================================
-- 初始化组卷规则
-- ============================================================
INSERT INTO `exam_rule` (`exam_id`, `question_type`, `difficulty`, `count`, `score_per_question`) VALUES
-- 考试1: 3单选 + 2多选 + 2判断 = 80分
(1, 'single', 1, 3, 10),
(1, 'multiple', NULL, 2, 15),
(1, 'true_false', NULL, 2, 5),
-- 考试2: 3单选 + 2判断 = 50分
(2, 'single', NULL, 3, 10),
(2, 'true_false', NULL, 2, 10),
-- 考试3: 3单选 + 1多选 + 2判断 = 60分
(3, 'single', 1, 3, 10),
(3, 'multiple', 1, 1, 10),
(3, 'true_false', NULL, 2, 10),
-- 考试4: 3单选 + 2多选 + 2判断 = 100分
(4, 'single', NULL, 3, 20),
(4, 'multiple', NULL, 2, 15),
(4, 'true_false', NULL, 2, 10),
-- 考试5(草稿): 4单选 + 2多选 + 3判断
(5, 'single', NULL, 4, 10),
(5, 'multiple', NULL, 2, 15),
(5, 'true_false', NULL, 3, 10),
-- 考试6(草稿): 3单选 + 2多选 + 1判断
(6, 'single', 2, 3, 20),
(6, 'multiple', 2, 2, 20),
(6, 'true_false', NULL, 1, 10);

-- ============================================================
-- 初始化考试题目关联
-- ============================================================
-- 考试1: 7题 (3单选 + 2多选 + 2判断)
INSERT INTO `exam_question` (`exam_id`, `question_id`, `score`, `sort_order`) VALUES
(1, 1, 10, 1), (1, 2, 10, 2), (1, 3, 10, 3),
(1, 6, 15, 4), (1, 7, 15, 5),
(1, 11, 5, 6), (1, 12, 5, 7);

-- 考试2: 5题 (3单选 + 2判断)
INSERT INTO `exam_question` (`exam_id`, `question_id`, `score`, `sort_order`) VALUES
(2, 4, 10, 1), (2, 8, 10, 2), (2, 9, 10, 3),
(2, 13, 10, 4), (2, 14, 10, 5);

-- 考试3: 6题 (3单选 + 1多选 + 2判断) — 网络主题
INSERT INTO `exam_question` (`exam_id`, `question_id`, `score`, `sort_order`) VALUES
(3, 16, 10, 1), (3, 17, 10, 2), (3, 18, 10, 3),
(3, 23, 10, 4),
(3, 37, 10, 5), (3, 40, 10, 6);

-- 考试4: 7题 (3单选 + 2多选 + 2判断) — 数据库主题
INSERT INTO `exam_question` (`exam_id`, `question_id`, `score`, `sort_order`) VALUES
(4, 13, 20, 1), (4, 14, 20, 2), (4, 15, 20, 3),
(4, 26, 15, 4), (4, 30, 15, 5),
(4, 33, 10, 6), (4, 38, 10, 7);

-- 考试5(草稿): 9题
INSERT INTO `exam_question` (`exam_id`, `question_id`, `score`, `sort_order`) VALUES
(5, 1, 10, 1), (5, 2, 10, 2), (5, 6, 10, 3), (5, 7, 10, 4),
(5, 21, 15, 6),
(5, 11, 10, 7), (5, 12, 10, 8), (5, 13, 10, 9);

-- 考试6(草稿): 6题
INSERT INTO `exam_question` (`exam_id`, `question_id`, `score`, `sort_order`) VALUES
(6, 11, 20, 1), (6, 12, 20, 2), (6, 13, 20, 3),
(6, 28, 20, 4), (6, 29, 20, 5),
(6, 16, 10, 6);

-- ============================================================
-- 初始化考试记录
-- ============================================================
-- 李同学(id=4): 参加3场考试 (1已批改65分, 2进行中, 3已批改80分)
-- 王同学(id=5): 参加2场考试 (1已批改50分, 4已批改90分)
-- 赵同学(id=6): 参加1场考试 (1已批改75分)
INSERT INTO `exam_record` (`id`, `exam_id`, `student_id`, `start_time`, `submit_time`, `status`, `total_score`, `create_time`, `update_time`) VALUES
-- 李同学
(1, 1, 4, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 'graded', 65, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 2, 4, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL, 'in_progress', NULL, DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(3, 3, 4, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 'graded', 50, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
-- 王同学
(4, 1, 5, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 'graded', 50, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 4, 5, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 HOUR), 'graded', 90, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 HOUR)),
-- 赵同学
(6, 1, 6, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 'graded', 75, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 3, 6, DATE_SUB(NOW(), INTERVAL 12 HOUR), NULL, 'in_progress', NULL, DATE_SUB(NOW(), INTERVAL 12 HOUR), DATE_SUB(NOW(), INTERVAL 12 HOUR));

-- ============================================================
-- 初始化答题记录
-- ============================================================
-- 考试记录1: 李同学考考试1 (7题, 65分)
INSERT INTO `exam_answer` (`record_id`, `question_id`, `student_answer`, `is_correct`, `score`) VALUES
(1, 1, 'A', 1, 10),
(1, 2, 'C', 1, 10),
(1, 3, 'C', 1, 10),
(1, 6, 'A,B', 0, 0),
(1, 7, 'A,B,C,D', 1, 15),
(1, 11, 'A', 1, 5),
(1, 12, 'B', 0, 0);

-- 考试记录3: 李同学考考试3 (6题, 50分)
INSERT INTO `exam_answer` (`record_id`, `question_id`, `student_answer`, `is_correct`, `score`) VALUES
(3, 16, 'A', 1, 10),
(3, 17, 'C', 1, 10),
(3, 18, 'B', 1, 10),
(3, 23, 'A,B,C,D', 1, 10),
(3, 37, 'B', 0, 0),
(3, 40, 'A', 1, 10);

-- 考试记录4: 王同学考考试1 (7题, 50分)
INSERT INTO `exam_answer` (`record_id`, `question_id`, `student_answer`, `is_correct`, `score`) VALUES
(4, 1, 'A', 1, 10),
(4, 2, 'B', 0, 0),
(4, 3, 'C', 1, 10),
(4, 6, 'A,B,C', 1, 15),
(4, 7, 'A,B,C', 0, 0),
(4, 11, 'A', 1, 5),
(4, 12, 'A', 1, 5);

-- 考试记录5: 王同学考考试4 (7题, 85分)
INSERT INTO `exam_answer` (`record_id`, `question_id`, `student_answer`, `is_correct`, `score`) VALUES
(5, 13, 'C', 1, 20),
(5, 14, 'B', 0, 0),
(5, 15, 'B', 1, 20),
(5, 26, 'A,B,C', 1, 15),
(5, 30, 'A,B,C,D', 1, 15),
(5, 33, 'A', 1, 10),
(5, 38, 'A', 1, 10);

-- 考试记录6: 赵同学考考试1 (7题, 75分)
INSERT INTO `exam_answer` (`record_id`, `question_id`, `student_answer`, `is_correct`, `score`) VALUES
(6, 1, 'A', 1, 10),
(6, 2, 'C', 1, 10),
(6, 3, 'B', 0, 0),
(6, 6, 'A,B,C', 1, 15),
(6, 7, 'A,B,C,D', 1, 15),
(6, 11, 'A', 1, 5),
(6, 12, 'A', 1, 5);