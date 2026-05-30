# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 技术栈
### 后端
- Java 17
- SpringBoot 3.2.x
- MyBatis-Plus 3.5.x
- Maven 3.9.x
- MySQL 8.0
- 统一返回：Result<T>
- 端口：8080

## 目录结构
### backend
src/main/java/com/example/
├── controller/
├── service/
├── service/impl/
├── mapper/
├── entity/
├── dto/
├── vo/
├── config/
└── common/

## 编码规范
### 后端
- RESTful 接口，路径以 /api/v1 开头
- 使用 Lombok，禁止手写 getter/setter
- 入参校验用 @NotBlank、@NotNull
- 统一异常处理 @ControllerAdvice
- SQL 用 MyBatis-Plus，禁止写 *

## 常用命令
- 后端启动：mvn spring-boot:run