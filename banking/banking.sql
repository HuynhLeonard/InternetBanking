CREATE DATABASE  IF NOT EXISTS `banking` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `banking`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: banking
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` varchar(255) NOT NULL,
  `accountNumber` varchar(255) NOT NULL,
  `customerId` varchar(255) NOT NULL,
  `balance` bigint NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `accountNumber_UNIQUE` (`accountNumber`),
  UNIQUE KEY `customerId_UNIQUE` (`customerId`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES ('550e8400-e29b-41d4-a716-446655440000','AC001','C001',5000000,'2024-12-19 08:10:00',NULL),('123e4567-e89b-12d3-a456-426614174000','AC002','C002',10000000,'2024-12-19 08:40:00',NULL),('9f8c7d6a-5b4e-3c2d-1a0b-9e8d7c6b5a4f','AC003','C003',7500000,'2024-12-19 09:10:00',NULL),('3fa85f64-5717-4562-b3fc-2c963f66afa6','AC004','C004',20000000,'2024-12-19 09:40:00',NULL),('6a1b2c3d-4e5f-6789-abcd-ef0123456789','AC005','C005',1500000,'2024-12-19 10:10:00',NULL);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank`
--

DROP TABLE IF EXISTS `bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `shortName` varchar(255) DEFAULT NULL,
  `urlInfo` varchar(255) NOT NULL,
  `urlTransaction` varchar(255) NOT NULL,
  `localSecretKey` varchar(255) NOT NULL,
  `foreignSecretKey` varchar(255) NOT NULL,
  `foreignPublicKey` varchar(255) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updateAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  UNIQUE KEY `shortName_UNIQUE` (`shortName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank`
--

LOCK TABLES `bank` WRITE;
/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phoneNumber` varchar(255) DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES ('C001','Huỳnh Thiện Hữu','huynhthienhuu@gmail.com','password123','0909123456','123 Le Loi, District 1, HCMC','2024-12-19 08:00:00',NULL),('C002','Đinh Thế Anh','anhdt@gmail.com','password123','0912233445','456 Hai Ba Trung, District 3, HCMC','2024-12-19 08:30:00',NULL),('C003','Võ Tấn Lộc','locvt@gmail.com','password123','0922123456','789 Vo Van Tan, District 5, HCMC','2024-12-19 09:00:00',NULL),('C004','Nguyễn Thuận Phát','phatnt@gmail.com','password123','0933233445','321 Nguyen Hue, District 1, HCMC','2024-12-19 09:30:00',NULL),('C005','Lê Tự Anh Tuấn','tuanlta@gmail.com','password123','0944123456','654 Dien Bien Phu, District 10, HCMC','2024-12-19 10:00:00',NULL);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dept_reminder`
--

DROP TABLE IF EXISTS `dept_reminder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dept_reminder` (
  `id` int NOT NULL AUTO_INCREMENT,
  `senderAccountId` varchar(255) NOT NULL,
  `receiverAccountId` varchar(255) NOT NULL,
  `amount` bigint NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `status` enum('paid','unpaid') NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `senderAccountNumber` (`senderAccountId`),
  KEY `receiverAccountNumber` (`receiverAccountId`),
  CONSTRAINT `dept_reminder_ibfk_1` FOREIGN KEY (`senderAccountId`) REFERENCES `account` (`id`),
  CONSTRAINT `dept_reminder_ibfk_2` FOREIGN KEY (`receiverAccountId`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dept_reminder`
--

LOCK TABLES `dept_reminder` WRITE;
/*!40000 ALTER TABLE `dept_reminder` DISABLE KEYS */;
/*!40000 ALTER TABLE `dept_reminder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `otp`
--

DROP TABLE IF EXISTS `otp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `otp` varchar(255) NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  `expiredAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otp`
--

LOCK TABLES `otp` WRITE;
/*!40000 ALTER TABLE `otp` DISABLE KEYS */;
/*!40000 ALTER TABLE `otp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receiver`
--

DROP TABLE IF EXISTS `receiver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receiver` (
  `id` int NOT NULL AUTO_INCREMENT,
  `senderAccountId` varchar(255) NOT NULL,
  `receiverAccountId` varchar(255) NOT NULL,
  `nickName` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `senderAccountNumber` (`senderAccountId`),
  KEY `receiverAccountNumber` (`receiverAccountId`),
  CONSTRAINT `receiver_ibfk_1` FOREIGN KEY (`senderAccountId`) REFERENCES `account` (`id`),
  CONSTRAINT `receiver_ibfk_2` FOREIGN KEY (`receiverAccountId`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receiver`
--

LOCK TABLES `receiver` WRITE;
/*!40000 ALTER TABLE `receiver` DISABLE KEYS */;
/*!40000 ALTER TABLE `receiver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_provider`
--

DROP TABLE IF EXISTS `service_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_provider` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `phoneNumber` varchar(255) DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `updatedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_provider`
--

LOCK TABLES `service_provider` WRITE;
/*!40000 ALTER TABLE `service_provider` DISABLE KEYS */;
INSERT INTO `service_provider` VALUES ('sp001','Admin','thienhuuhuynh@gmail.com','thienhuu2003','admin','02812345678','123 Main Branch, District 1, HCMC','2024-12-19 08:00:00',NULL),('sp002','TuanLTA','ltatuan@gmail.com','anhtuan2003','employee','02887654321','456 Branch 2, District 3, HCMC','2024-12-19 08:15:00','2024-12-19 09:00:00'),('sp003','LocVT','vtloc@gmail.com','tanloc2003','employee','02833445566','789 Branch 3, District 5, HCMC','2024-12-19 08:30:00','2024-12-19 09:30:00'),('sp004','AnhDT','dtanh@gmail.com','theanh2003','employee','02899887766','321 Main Admin Office, District 1, HCMC','2024-12-19 08:45:00',NULL),('sp005','PhatNT','ntphat@gmail.com','thuanphat2003','employee','02855667788','654 Support Office, District 10, HCMC','2024-12-19 09:00:00','2024-12-19 10:00:00');
/*!40000 ALTER TABLE `service_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `senderAccountId` varchar(255) NOT NULL,
  `receiverAccountId` varchar(255) NOT NULL,
  `amount` bigint NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `type` enum('internal','external','dept','deposit') NOT NULL,
  `bankId` int DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `senderAccountNumber` (`senderAccountId`),
  KEY `receiverAccountNumber` (`receiverAccountId`),
  KEY `bankId` (`bankId`),
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`senderAccountId`) REFERENCES `account` (`id`),
  CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`receiverAccountId`) REFERENCES `account` (`id`),
  CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`bankId`) REFERENCES `bank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--
LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;

DROP TABLE IF EXISTS `employee_transaction`;

CREATE TABLE `employee_transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `serviceProviderId` varchar(255) NOT NULL,
  `receiverAccountId` varchar(255) NOT NULL,
  `amount` bigint NOT NULL,
  `createdAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `employeeNumber` (`serviceProviderId`),
  KEY `receiverAccountNumber` (`receiverAccountId`),
  CONSTRAINT `transaction_ibfk_4` FOREIGN KEY (`serviceProviderId`) REFERENCES `service_provider` (`id`),
  CONSTRAINT `transaction_ibfk_5` FOREIGN KEY (`receiverAccountId`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `employee_transaction` WRITE;
/*!40000 ALTER TABLE `employee_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `employee_transaction` ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS `external_transaction`;

CREATE TABLE `external_transaction` (
    `id` int NOT NULL AUTO_INCREMENT,
    `bankId` int NOT NULL,
    `accountNumber` varchar(255) NOT NULL,
    `foreignAccountNumber` varchar(255) NOT NULL,
    `amount` bigint NOT NULL,
    `theirSignature` varchar(255) not null,
    `type` varchar(255) NOT NULL,
    `createdAt` datetime DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `bank` (`bankId`),
    CONSTRAINT `transaction_ibfk_6` FOREIGN KEY (`bankId`) REFERENCES `bank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `external_transaction` WRITE;
/*!40000 ALTER TABLE `external_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `external_transaction` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-21 15:05:49
