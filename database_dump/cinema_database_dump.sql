CREATE DATABASE  IF NOT EXISTS `cinema_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `cinema_db`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cinema_db
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `halls`
--

DROP TABLE IF EXISTS `halls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `halls` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_seats` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `halls`
--

LOCK TABLES `halls` WRITE;
/*!40000 ALTER TABLE `halls` DISABLE KEYS */;
INSERT INTO `halls` VALUES (1,'Hall 1',100),(2,'Hall 2',60),(3,'Hall 3',80);
/*!40000 ALTER TABLE `halls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `duration_minutes` int NOT NULL,
  `rating` decimal(2,1) DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `image_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `movies_chk_1` CHECK ((`duration_minutes` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movies`
--

LOCK TABLES `movies` WRITE;
/*!40000 ALTER TABLE `movies` DISABLE KEYS */;
INSERT INTO `movies` VALUES (1,'Minions: The Rise of Gru','Minions: The Rise of Gru (2022) is a fun, animated movie where Gru, a young boy with dreams of becoming the world’s greatest supervillain, teams up with his lovable minions to pull off mischievous, child-friendly plans.',87,6.7,'2022-07-01','rise_of_gru.jpg'),(2,'Toy Story','Toy Story (1995) is a heartwarming animated film about a group of toys that come to life when their owner is not around. Woody, a cowboy doll, and Buzz Lightyear, a space ranger action figure, must put aside their differences and work together when a new toy threatens Woody\'s place as the favorite.',81,8.3,'1995-11-22','toy_story.jpg'),(3,'The Lion King','The Lion King (1994) is a beloved animated classic about Simba, a young lion who must overcome adversity and take his place as the rightful king of the Pride Lands. Along with his friends Timon and Pumbaa, Simba learns valuable life lessons about responsibility and courage.',88,8.5,'1994-06-15','lion_king.jpg'),(4,'Zootopia','Zootopia (2016) is an animated film set in a city where animals of all kinds coexist. The story follows Judy Hopps, a bunny who becomes the first of her kind to join the police force, and her unlikely partnership with a fox named Nick Wilde as they solve a mystery.',108,8.0,'2016-03-17','zootopia.jpg');
/*!40000 ALTER TABLE `movies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `screenings`
--

DROP TABLE IF EXISTS `screenings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `screenings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `movie_id` bigint NOT NULL,
  `hall_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `price` decimal(8,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_screening_movie` (`movie_id`),
  KEY `fk_screening_hall` (`hall_id`),
  CONSTRAINT `fk_screening_hall` FOREIGN KEY (`hall_id`) REFERENCES `halls` (`id`),
  CONSTRAINT `fk_screening_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `screenings`
--

LOCK TABLES `screenings` WRITE;
/*!40000 ALTER TABLE `screenings` DISABLE KEYS */;
INSERT INTO `screenings` VALUES (1,1,1,'2026-03-01 14:00:00',10.00),(2,2,2,'2026-03-01 14:30:00',12.50),(3,3,3,'2026-03-01 16:30:00',15.00),(4,4,1,'2026-03-01 18:30:00',11.00),(5,1,1,'2026-03-02 14:15:00',10.00),(6,1,1,'2026-03-02 16:30:00',10.00),(7,2,2,'2026-03-02 14:45:00',12.50),(8,2,2,'2026-03-02 17:00:00',12.50),(9,3,3,'2026-03-02 15:00:00',15.00),(10,3,3,'2026-03-02 17:30:00',15.00),(11,4,1,'2026-03-02 18:45:00',11.00),(12,4,1,'2026-03-02 20:30:00',11.00);
/*!40000 ALTER TABLE `screenings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seats`
--

DROP TABLE IF EXISTS `seats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hall_id` bigint NOT NULL,
  `row_number` int NOT NULL,
  `seat_number` int NOT NULL,
  `seat_type` enum('STANDARD','VIP') COLLATE utf8mb4_unicode_ci DEFAULT 'STANDARD',
  PRIMARY KEY (`id`),
  UNIQUE KEY `hall_id` (`hall_id`,`row_number`,`seat_number`),
  CONSTRAINT `fk_seat_hall` FOREIGN KEY (`hall_id`) REFERENCES `halls` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=271 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seats`
--

LOCK TABLES `seats` WRITE;
/*!40000 ALTER TABLE `seats` DISABLE KEYS */;
INSERT INTO `seats` VALUES (1,1,1,1,'STANDARD'),(2,1,1,2,'STANDARD'),(3,1,1,3,'STANDARD'),(4,1,1,4,'STANDARD'),(5,1,1,5,'STANDARD'),(6,1,1,6,'STANDARD'),(7,1,1,7,'STANDARD'),(8,1,1,8,'STANDARD'),(9,1,1,9,'STANDARD'),(10,1,1,10,'STANDARD'),(11,1,2,1,'STANDARD'),(12,1,2,2,'STANDARD'),(13,1,2,3,'STANDARD'),(14,1,2,4,'STANDARD'),(15,1,2,5,'STANDARD'),(16,1,2,6,'STANDARD'),(17,1,2,7,'STANDARD'),(18,1,2,8,'STANDARD'),(19,1,2,9,'STANDARD'),(20,1,2,10,'STANDARD'),(21,1,3,1,'STANDARD'),(22,1,3,2,'STANDARD'),(23,1,3,3,'STANDARD'),(24,1,3,4,'STANDARD'),(25,1,3,5,'STANDARD'),(26,1,3,6,'STANDARD'),(27,1,3,7,'STANDARD'),(28,1,3,8,'STANDARD'),(29,1,3,9,'STANDARD'),(30,1,3,10,'STANDARD'),(31,1,4,1,'STANDARD'),(32,1,4,2,'STANDARD'),(33,1,4,3,'STANDARD'),(34,1,4,4,'STANDARD'),(35,1,4,5,'STANDARD'),(36,1,4,6,'STANDARD'),(37,1,4,7,'STANDARD'),(38,1,4,8,'STANDARD'),(39,1,4,9,'STANDARD'),(40,1,4,10,'STANDARD'),(41,1,5,1,'STANDARD'),(42,1,5,2,'STANDARD'),(43,1,5,3,'STANDARD'),(44,1,5,4,'STANDARD'),(45,1,5,5,'STANDARD'),(46,1,5,6,'STANDARD'),(47,1,5,7,'STANDARD'),(48,1,5,8,'STANDARD'),(49,1,5,9,'STANDARD'),(50,1,5,10,'STANDARD'),(51,1,6,1,'STANDARD'),(52,1,6,2,'STANDARD'),(53,1,6,3,'STANDARD'),(54,1,6,4,'STANDARD'),(55,1,6,5,'STANDARD'),(56,1,6,6,'STANDARD'),(57,1,6,7,'STANDARD'),(58,1,6,8,'STANDARD'),(59,1,6,9,'STANDARD'),(60,1,6,10,'STANDARD'),(61,1,7,1,'STANDARD'),(62,1,7,2,'STANDARD'),(63,1,7,3,'STANDARD'),(64,1,7,4,'STANDARD'),(65,1,7,5,'STANDARD'),(66,1,7,6,'STANDARD'),(67,1,7,7,'STANDARD'),(68,1,7,8,'STANDARD'),(69,1,7,9,'STANDARD'),(70,1,7,10,'STANDARD'),(71,1,8,1,'STANDARD'),(72,1,8,2,'STANDARD'),(73,1,8,3,'STANDARD'),(74,1,8,4,'STANDARD'),(75,1,8,5,'STANDARD'),(76,1,8,6,'STANDARD'),(77,1,8,7,'STANDARD'),(78,1,8,8,'STANDARD'),(79,1,8,9,'STANDARD'),(80,1,8,10,'STANDARD'),(81,1,9,1,'STANDARD'),(82,1,9,2,'STANDARD'),(83,1,9,3,'STANDARD'),(84,1,9,4,'STANDARD'),(85,1,9,5,'STANDARD'),(86,1,9,6,'STANDARD'),(87,1,9,7,'STANDARD'),(88,1,9,8,'STANDARD'),(89,1,9,9,'STANDARD'),(90,1,9,10,'STANDARD'),(91,1,10,1,'STANDARD'),(92,1,10,2,'STANDARD'),(93,1,10,3,'STANDARD'),(94,1,10,4,'STANDARD'),(95,1,10,5,'STANDARD'),(96,1,10,6,'STANDARD'),(97,1,10,7,'STANDARD'),(98,1,10,8,'STANDARD'),(99,1,10,9,'STANDARD'),(100,1,10,10,'STANDARD'),(128,2,1,1,'STANDARD'),(129,2,1,2,'STANDARD'),(130,2,1,3,'STANDARD'),(131,2,1,4,'STANDARD'),(132,2,1,5,'STANDARD'),(133,2,1,6,'STANDARD'),(134,2,1,7,'STANDARD'),(135,2,1,8,'STANDARD'),(136,2,1,9,'STANDARD'),(137,2,1,10,'STANDARD'),(138,2,2,1,'STANDARD'),(139,2,2,2,'STANDARD'),(140,2,2,3,'STANDARD'),(141,2,2,4,'STANDARD'),(142,2,2,5,'STANDARD'),(143,2,2,6,'STANDARD'),(144,2,2,7,'STANDARD'),(145,2,2,8,'STANDARD'),(146,2,2,9,'STANDARD'),(147,2,2,10,'STANDARD'),(148,2,3,1,'STANDARD'),(149,2,3,2,'STANDARD'),(150,2,3,3,'STANDARD'),(151,2,3,4,'STANDARD'),(152,2,3,5,'STANDARD'),(153,2,3,6,'STANDARD'),(154,2,3,7,'STANDARD'),(155,2,3,8,'STANDARD'),(156,2,3,9,'STANDARD'),(157,2,3,10,'STANDARD'),(158,2,4,1,'STANDARD'),(159,2,4,2,'STANDARD'),(160,2,4,3,'STANDARD'),(161,2,4,4,'STANDARD'),(162,2,4,5,'STANDARD'),(163,2,4,6,'STANDARD'),(164,2,4,7,'STANDARD'),(165,2,4,8,'STANDARD'),(166,2,4,9,'STANDARD'),(167,2,4,10,'STANDARD'),(168,2,5,1,'STANDARD'),(169,2,5,2,'STANDARD'),(170,2,5,3,'STANDARD'),(171,2,5,4,'STANDARD'),(172,2,5,5,'STANDARD'),(173,2,5,6,'STANDARD'),(174,2,5,7,'STANDARD'),(175,2,5,8,'STANDARD'),(176,2,5,9,'STANDARD'),(177,2,5,10,'STANDARD'),(178,2,6,1,'STANDARD'),(179,2,6,2,'STANDARD'),(180,2,6,3,'STANDARD'),(181,2,6,4,'STANDARD'),(182,2,6,5,'STANDARD'),(183,2,6,6,'STANDARD'),(184,2,6,7,'STANDARD'),(185,2,6,8,'STANDARD'),(186,2,6,9,'STANDARD'),(187,2,6,10,'STANDARD'),(191,3,1,1,'STANDARD'),(192,3,1,2,'STANDARD'),(193,3,1,3,'STANDARD'),(194,3,1,4,'STANDARD'),(195,3,1,5,'STANDARD'),(196,3,1,6,'STANDARD'),(197,3,1,7,'STANDARD'),(198,3,1,8,'STANDARD'),(199,3,1,9,'STANDARD'),(200,3,1,10,'STANDARD'),(201,3,2,1,'STANDARD'),(202,3,2,2,'STANDARD'),(203,3,2,3,'STANDARD'),(204,3,2,4,'STANDARD'),(205,3,2,5,'STANDARD'),(206,3,2,6,'STANDARD'),(207,3,2,7,'STANDARD'),(208,3,2,8,'STANDARD'),(209,3,2,9,'STANDARD'),(210,3,2,10,'STANDARD'),(211,3,3,1,'STANDARD'),(212,3,3,2,'STANDARD'),(213,3,3,3,'STANDARD'),(214,3,3,4,'STANDARD'),(215,3,3,5,'STANDARD'),(216,3,3,6,'STANDARD'),(217,3,3,7,'STANDARD'),(218,3,3,8,'STANDARD'),(219,3,3,9,'STANDARD'),(220,3,3,10,'STANDARD'),(221,3,4,1,'STANDARD'),(222,3,4,2,'STANDARD'),(223,3,4,3,'STANDARD'),(224,3,4,4,'STANDARD'),(225,3,4,5,'STANDARD'),(226,3,4,6,'STANDARD'),(227,3,4,7,'STANDARD'),(228,3,4,8,'STANDARD'),(229,3,4,9,'STANDARD'),(230,3,4,10,'STANDARD'),(231,3,5,1,'STANDARD'),(232,3,5,2,'STANDARD'),(233,3,5,3,'STANDARD'),(234,3,5,4,'STANDARD'),(235,3,5,5,'STANDARD'),(236,3,5,6,'STANDARD'),(237,3,5,7,'STANDARD'),(238,3,5,8,'STANDARD'),(239,3,5,9,'STANDARD'),(240,3,5,10,'STANDARD'),(241,3,6,1,'STANDARD'),(242,3,6,2,'STANDARD'),(243,3,6,3,'STANDARD'),(244,3,6,4,'STANDARD'),(245,3,6,5,'STANDARD'),(246,3,6,6,'STANDARD'),(247,3,6,7,'STANDARD'),(248,3,6,8,'STANDARD'),(249,3,6,9,'STANDARD'),(250,3,6,10,'STANDARD'),(251,3,7,1,'STANDARD'),(252,3,7,2,'STANDARD'),(253,3,7,3,'STANDARD'),(254,3,7,4,'STANDARD'),(255,3,7,5,'STANDARD'),(256,3,7,6,'STANDARD'),(257,3,7,7,'STANDARD'),(258,3,7,8,'STANDARD'),(259,3,7,9,'STANDARD'),(260,3,7,10,'STANDARD'),(261,3,8,1,'STANDARD'),(262,3,8,2,'STANDARD'),(263,3,8,3,'STANDARD'),(264,3,8,4,'STANDARD'),(265,3,8,5,'STANDARD'),(266,3,8,6,'STANDARD'),(267,3,8,7,'STANDARD'),(268,3,8,8,'STANDARD'),(269,3,8,9,'STANDARD'),(270,3,8,10,'STANDARD');
/*!40000 ALTER TABLE `seats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `screening_id` bigint NOT NULL,
  `seat_id` bigint NOT NULL,
  `booked_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('BOOKED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'BOOKED',
  PRIMARY KEY (`id`),
  UNIQUE KEY `screening_id` (`screening_id`,`seat_id`),
  KEY `fk_ticket_user` (`user_id`),
  KEY `fk_ticket_seat` (`seat_id`),
  CONSTRAINT `fk_ticket_screening` FOREIGN KEY (`screening_id`) REFERENCES `screenings` (`id`),
  CONSTRAINT `fk_ticket_seat` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`),
  CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
INSERT INTO `tickets` VALUES (1,1,1,15,'2026-03-10 19:37:58','BOOKED'),(2,1,1,14,'2026-03-10 19:43:14','BOOKED'),(4,1,1,22,'2026-03-17 21:06:48','BOOKED'),(88,3,5,1,'2026-04-11 19:32:16','BOOKED'),(89,3,5,2,'2026-04-11 19:33:03','BOOKED'),(90,3,5,3,'2026-04-11 19:34:03','BOOKED'),(91,34,10,191,'2026-04-11 19:34:13','BOOKED'),(92,35,4,55,'2026-04-11 19:35:15','BOOKED');
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('USER','ADMIN') COLLATE utf8mb4_unicode_ci DEFAULT 'USER',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'user@mail.com','123456','David Blake','USER','2026-02-14 18:13:48'),(2,'bob@example.com','password123','Bob Smith','ADMIN','2026-02-27 21:34:19'),(3,'admin@mail.com','123456','Charlie Brown','ADMIN','2026-03-10 19:08:37'),(34,'Jack1@gmail.com','cssa1PAss!','Jack Brady','USER','2026-04-11 19:17:28'),(35,'AnnaBrown@gmail.com','Dfs!gn41vb','Anna Brown','USER','2026-04-11 19:26:37');
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

-- Dump completed on 2026-04-21 11:38:01
