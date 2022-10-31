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

DROP TABLE IF EXISTS `replies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `replies` (
  `id_comment` bigint NOT NULL,
  `id_reply` bigint NOT NULL,
  UNIQUE KEY `UK_edgft57yym1su35ox8iagf2lp` (`id_reply`),
  UNIQUE KEY `UK8fwpmxowqbk75v70r7nwiqjny` (`id_comment`,`id_reply`),
  CONSTRAINT `fk_comment_reply` FOREIGN KEY (`id_reply`) REFERENCES `comments` (`id_comment`),
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
  `text` varchar(100) NOT NULL,
  `date` datetime NOT NULL,
  `id_user` bigint NOT NULL,
  `id_post` bigint NOT NULL,
  PRIMARY KEY (`id_comment`),
  KEY `fk_user_comment` (`id_user`),
  KEY `fk_post_comment_idx` (`id_post`),
  CONSTRAINT `fk_post_comment` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`),
  CONSTRAINT `fk_user_comment` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `date` date DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_friendship`),
  KEY `fk_user_transmitter` (`id_user_transmitter`),
  KEY `fk_user_receiver` (`id_user_receiver`),
  CONSTRAINT `fk_user_receiver` FOREIGN KEY (`id_user_receiver`) REFERENCES `users` (`id_user`),
  CONSTRAINT `fk_user_transmitter` FOREIGN KEY (`id_user_transmitter`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `date` datetime NOT NULL,
  PRIMARY KEY (`id_like`),
  KEY `fk_post_like` (`id_post`),
  KEY `fk_user_like` (`id_user`),
  CONSTRAINT `fk_post_like` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`),
  CONSTRAINT `fk_user_like` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `date` datetime NOT NULL,
  `photo` varchar(100) DEFAULT NULL,
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
  `is_viewed` tinyint(1) NOT NULL DEFAULT '0',
  `date` datetime NOT NULL,
  `notification_type` varchar(50) NOT NULL,
  `id_user_friend` bigint DEFAULT NULL,
  `id_user_message_transmitter` bigint DEFAULT NULL,
  `id_post` bigint DEFAULT NULL,
  `id_friendship` bigint DEFAULT NULL,
  `id_comment` bigint DEFAULT NULL,
  PRIMARY KEY (`id_notification`),
  KEY `fk_user_user-friend` (`id_user_friend`),
  KEY `fk_user_user-msg` (`id_user_message_transmitter`),
  KEY `fk_post_notification` (`id_post`),
  KEY `fk_friendship_notification_idx` (`id_friendship`),
  KEY `fk_user-notif_notif_idx` (`id_user_receiver`),
  KEY `fk_comment_notif_idx` (`id_comment`),
  CONSTRAINT `fk_comment_notif` FOREIGN KEY (`id_comment`) REFERENCES `comments` (`id_comment`),
  CONSTRAINT `fk_friendship_notification` FOREIGN KEY (`id_friendship`) REFERENCES `friendships` (`id_friendship`),
  CONSTRAINT `fk_post_notification` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`),
  CONSTRAINT `fk_user-notif_notif` FOREIGN KEY (`id_user_receiver`) REFERENCES `users` (`id_user`),
  CONSTRAINT `fk_user_user-friend` FOREIGN KEY (`id_user_friend`) REFERENCES `users` (`id_user`),
  CONSTRAINT `fk_user_user-msg` FOREIGN KEY (`id_user_message_transmitter`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,'Welcome to Social App! start adding some friends, you can search for people using the search bar, have fun!','79b2a43b-1b31-4be8-bd67-9c9c9e536881_fdgh.png','2022-10-28 14:25:43.319000',1);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_reasons`
--

DROP TABLE IF EXISTS `report_reasons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_reasons` (
  `id_report_reason` int NOT NULL AUTO_INCREMENT,
  `reason` varchar(45) NOT NULL,
  PRIMARY KEY (`id_report_reason`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_reasons`
--

LOCK TABLES `report_reasons` WRITE;
/*!40000 ALTER TABLE `report_reasons` DISABLE KEYS */;
INSERT INTO `report_reasons` VALUES (1,'Spam'),(2,'Nudity or sexual activity'),(3,'Violence, blood or bullying'),(4,'Sale of illegal items'),(5,'Copyright'),(6,'Fraud');
/*!40000 ALTER TABLE `report_reasons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `id_report` bigint NOT NULL AUTO_INCREMENT,
  `id_post` bigint NOT NULL,
  `id_user` bigint NOT NULL,
  `id_report_reason` int NOT NULL,
  `extra_information` varchar(200) DEFAULT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id_report`),
  KEY `fk_post_report_idx` (`id_post`),
  KEY `fk_user_report_idx` (`id_user`),
  KEY `fk_reason_report_idx` (`id_report_reason`),
  CONSTRAINT `fk_post_report` FOREIGN KEY (`id_post`) REFERENCES `posts` (`id_post`),
  CONSTRAINT `fk_reason_report` FOREIGN KEY (`id_report_reason`) REFERENCES `report_reasons` (`id_report_reason`),
  CONSTRAINT `fk_user_report` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status` (
  `id_status` int NOT NULL AUTO_INCREMENT,
  `text` varchar(15) NOT NULL,
  PRIMARY KEY (`id_status`),
  UNIQUE KEY `text_UNIQUE` (`text`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status`
--

LOCK TABLES `status` WRITE;
/*!40000 ALTER TABLE `status` DISABLE KEYS */;
INSERT INTO `status` VALUES (1,'Connected'),(2,'Disconnected'),(3,'On call');
/*!40000 ALTER TABLE `status` ENABLE KEYS */;
UNLOCK TABLES;

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
  `creation_date` datetime(6) NOT NULL,
  `deletion_date` datetime(6) DEFAULT NULL,
  `is_checked` tinyint NOT NULL,
  `id_status` int NOT NULL DEFAULT '2',
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`username`),
  KEY `fk_status_user_idx` (`id_status`),
  CONSTRAINT `fk_status_user` FOREIGN KEY (`id_status`) REFERENCES `status` (`id_status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','Social','App','b4b7cac7-433a-4ef3-be1a-4ffc93de7352_1666966437308.jpeg','¡Welcome to Social App!','2022-08-15 18:12:41.005000',NULL,1,'Disconnected');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-09-22 17:39:56
