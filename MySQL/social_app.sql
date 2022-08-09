-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: social_app
-- ------------------------------------------------------
-- Server version	8.0.30

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
-- Table structure for table `answers`
--

DROP TABLE IF EXISTS `answers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `answers` (
  `id_comment` bigint NOT NULL,
  `id_answer` bigint NOT NULL,
  UNIQUE KEY `UK_edgft57yym1su35ox8iagf2lp` (`id_answer`),
  UNIQUE KEY `UK8fwpmxowqbk75v70r7nwiqjny` (`id_comment`,`id_answer`),
  CONSTRAINT `fk_comment_answer` FOREIGN KEY (`id_answer`) REFERENCES `comments` (`id_comment`),
  CONSTRAINT `fk_comment_comment` FOREIGN KEY (`id_comment`) REFERENCES `comments` (`id_comment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id_comment` bigint NOT NULL AUTO_INCREMENT,
  `text` varchar(50) NOT NULL,
  `id_user` bigint NOT NULL,
  `id_post` bigint NOT NULL,
  PRIMARY KEY (`id_comment`),
  KEY `fk_user_comment` (`id_user`),
  KEY `fk_post_comment_idx` (`id_post`),
  CONSTRAINT `fk_post_comment` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`),
  CONSTRAINT `fk_user_comment` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`),
  CONSTRAINT `FKl54ubqj6ovdw3hhjeyo3mnlgi` FOREIGN KEY (`id_comment`) REFERENCES `posts` (`id_post`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `friendships`
--

DROP TABLE IF EXISTS `friendships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friendships` (
  `id_friendship` bigint NOT NULL AUTO_INCREMENT,
  `id_user_transmitter` bigint NOT NULL,
  `id_user_receiver` bigint NOT NULL,
  `date` date NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_friendship`),
  KEY `fk_user_transmitter` (`id_user_transmitter`),
  KEY `fk_user_receiver` (`id_user_receiver`),
  CONSTRAINT `fk_user_receiver` FOREIGN KEY (`id_user_receiver`) REFERENCES `users` (`id_user`),
  CONSTRAINT `fk_user_transmitter` FOREIGN KEY (`id_user_transmitter`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `id_like` bigint NOT NULL AUTO_INCREMENT,
  `id_post` bigint NOT NULL,
  `id_user` bigint NOT NULL,
  PRIMARY KEY (`id_like`),
  KEY `fk_post_like` (`id_post`),
  KEY `fk_user_like` (`id_user`),
  CONSTRAINT `fk_post_like` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`),
  CONSTRAINT `fk_user_like` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id_message` bigint NOT NULL AUTO_INCREMENT,
  `text` varchar(50) NOT NULL,
  `id_user_receiver` bigint NOT NULL,
  `id_user_transmitter` bigint NOT NULL,
  PRIMARY KEY (`id_message`),
  KEY `fk_user_transmitter` (`id_user_transmitter`),
  KEY `fk_user_receiver` (`id_user_receiver`),
  CONSTRAINT `fk_user_msg-receiver` FOREIGN KEY (`id_user_receiver`) REFERENCES `users` (`id_user`),
  CONSTRAINT `fk_user_msg-transmitter` FOREIGN KEY (`id_user_transmitter`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id_notification` bigint NOT NULL AUTO_INCREMENT,
  `id_user_receiver` bigint NOT NULL,
  `is_viewed` tinyint(1) NOT NULL,
  `notification_type` varchar(50) NOT NULL,
  `id_user_friend` bigint DEFAULT NULL,
  `id_user_message_transmitter` bigint DEFAULT NULL,
  `id_post` bigint DEFAULT NULL,
  `notifications_id_notification` bigint NOT NULL,
  PRIMARY KEY (`id_notification`),
  UNIQUE KEY `UKf1apk00b8csmxlqide8rrra56` (`id_user_receiver`),
  UNIQUE KEY `UK_47yqfve80wbasx3x1txb7l2tw` (`notifications_id_notification`),
  KEY `fk_user_user-friend` (`id_user_friend`),
  KEY `fk_user_user-msg` (`id_user_message_transmitter`),
  KEY `fk_post_notification` (`id_post`),
  CONSTRAINT `FK8gpn2jg1k3o48rpjge33tqvxa` FOREIGN KEY (`notifications_id_notification`) REFERENCES `notifications` (`id_notification`),
  CONSTRAINT `fk_post_notification` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`),
  CONSTRAINT `fk_user_notif-receiver` FOREIGN KEY (`id_user_receiver`) REFERENCES `users` (`id_user`),
  CONSTRAINT `fk_user_user-friend` FOREIGN KEY (`id_user_friend`) REFERENCES `users` (`id_user`),
  CONSTRAINT `fk_user_user-msg` FOREIGN KEY (`id_user_message_transmitter`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id_post` bigint NOT NULL AUTO_INCREMENT,
  `text` varchar(200) DEFAULT NULL,
  `photo` varchar(100) DEFAULT NULL,
  `date` datetime(6) NOT NULL,
  `id_user` bigint NOT NULL,
  PRIMARY KEY (`id_post`),
  KEY `fk_user_post` (`id_user`),
  CONSTRAINT `fk_user_post` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id_user` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `name` varchar(30) NOT NULL,
  `surname` varchar(50) NOT NULL,
  `photo` varchar(100) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `creation_date` datetime(6) DEFAULT NULL,
  `deletion_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-08-08 18:40:03
