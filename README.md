# FoodTracker
An easy to use app designed to manage ingredients and recipes and keep track of the calories intake for a day.

# Table of Contents
* [Prerequisites](#prerequisites)
* [Getting started](#Getting-Started)
* [Database](#database)

# Prerequisites

* SQL Server
* Java 1.8

# Database
In order to use this app you need to create a local database in sql server with the following script:
```
USE [RecipesApp]
GO
/****** Object:  Table [dbo].[Account]    Script Date: 2/20/2018 1:09:45 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Account](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[username] [varchar](100) NOT NULL,
	[password] [varchar](100) NOT NULL,
	[email] [varchar](100) NOT NULL,
	[kcalGoal] [int] NULL,
 CONSTRAINT [PK_Account] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [Unique_email] UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [Unique_user] UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Account_ingredients]    Script Date: 2/20/2018 1:09:45 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Account_ingredients](
	[account_id] [int] NOT NULL,
	[ingredient_id] [int] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Account_recipes]    Script Date: 2/20/2018 1:09:45 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Account_recipes](
	[account_id] [int] NOT NULL,
	[recipe_id] [int] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DailyEntryIngredient]    Script Date: 2/20/2018 1:09:45 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DailyEntryIngredient](
	[id_account] [int] NOT NULL,
	[id_ingredient] [int] NOT NULL,
	[quantity] [float] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DailyEntryRecipe]    Script Date: 2/20/2018 1:09:45 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DailyEntryRecipe](
	[id_account] [int] NOT NULL,
	[id_recipe] [int] NOT NULL,
	[quantity] [float] NOT NULL
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Ingredient]    Script Date: 2/20/2018 1:09:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Ingredient](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [varchar](100) NOT NULL,
	[calories] [int] NOT NULL,
	[carbohydrates] [float] NULL,
	[proteins] [float] NULL,
	[fats] [float] NULL,
	[fibers] [float] NULL,
 CONSTRAINT [PK_Ingredient] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Permission]    Script Date: 2/20/2018 1:09:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Permission](
	[account_id] [int] NOT NULL,
	[permissionLevel] [int] NOT NULL,
 CONSTRAINT [PK_TrustedUser] PRIMARY KEY CLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Recipe]    Script Date: 2/20/2018 1:09:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Recipe](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [varchar](100) NOT NULL,
	[servings] [int] NOT NULL,
 CONSTRAINT [PK_Recipe] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Recipe_ingredients]    Script Date: 2/20/2018 1:09:46 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Recipe_ingredients](
	[ingredient_id] [int] NOT NULL,
	[recipe_id] [int] NOT NULL,
	[quantity] [int] NOT NULL
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Account_ingredients]  WITH CHECK ADD  CONSTRAINT [FK_Accountid] FOREIGN KEY([account_id])
REFERENCES [dbo].[Account] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Account_ingredients] CHECK CONSTRAINT [FK_Accountid]
GO
ALTER TABLE [dbo].[Account_ingredients]  WITH CHECK ADD  CONSTRAINT [FK_ingredient] FOREIGN KEY([ingredient_id])
REFERENCES [dbo].[Ingredient] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Account_ingredients] CHECK CONSTRAINT [FK_ingredient]
GO
ALTER TABLE [dbo].[Account_recipes]  WITH CHECK ADD  CONSTRAINT [FK_account] FOREIGN KEY([account_id])
REFERENCES [dbo].[Account] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Account_recipes] CHECK CONSTRAINT [FK_account]
GO
ALTER TABLE [dbo].[Account_recipes]  WITH CHECK ADD  CONSTRAINT [FK_recipe] FOREIGN KEY([recipe_id])
REFERENCES [dbo].[Recipe] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Account_recipes] CHECK CONSTRAINT [FK_recipe]
GO
ALTER TABLE [dbo].[DailyEntryIngredient]  WITH CHECK ADD  CONSTRAINT [FK_DailyEntryIngredient_Account] FOREIGN KEY([id_account])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[DailyEntryIngredient] CHECK CONSTRAINT [FK_DailyEntryIngredient_Account]
GO
ALTER TABLE [dbo].[DailyEntryIngredient]  WITH CHECK ADD  CONSTRAINT [FK_DailyEntryIngredient_Ingredient] FOREIGN KEY([id_ingredient])
REFERENCES [dbo].[Ingredient] ([id])
GO
ALTER TABLE [dbo].[DailyEntryIngredient] CHECK CONSTRAINT [FK_DailyEntryIngredient_Ingredient]
GO
ALTER TABLE [dbo].[DailyEntryRecipe]  WITH CHECK ADD  CONSTRAINT [FK_DailyEntryRecipe_Account] FOREIGN KEY([id_account])
REFERENCES [dbo].[Account] ([id])
GO
ALTER TABLE [dbo].[DailyEntryRecipe] CHECK CONSTRAINT [FK_DailyEntryRecipe_Account]
GO
ALTER TABLE [dbo].[DailyEntryRecipe]  WITH CHECK ADD  CONSTRAINT [FK_DailyEntryRecipe_recipe] FOREIGN KEY([id_recipe])
REFERENCES [dbo].[Recipe] ([id])
GO
ALTER TABLE [dbo].[DailyEntryRecipe] CHECK CONSTRAINT [FK_DailyEntryRecipe_recipe]
GO
ALTER TABLE [dbo].[Permission]  WITH CHECK ADD  CONSTRAINT [FK_TrustedUser_account] FOREIGN KEY([account_id])
REFERENCES [dbo].[Account] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Permission] CHECK CONSTRAINT [FK_TrustedUser_account]
GO
ALTER TABLE [dbo].[Recipe_ingredients]  WITH CHECK ADD  CONSTRAINT [fkey_ingredient] FOREIGN KEY([ingredient_id])
REFERENCES [dbo].[Ingredient] ([id])
GO
ALTER TABLE [dbo].[Recipe_ingredients] CHECK CONSTRAINT [fkey_ingredient]
GO
ALTER TABLE [dbo].[Recipe_ingredients]  WITH CHECK ADD  CONSTRAINT [fkey_recipe] FOREIGN KEY([recipe_id])
REFERENCES [dbo].[Recipe] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Recipe_ingredients] CHECK CONSTRAINT [fkey_recipe]
GO

```

# Getting Started
Creating a new account is done as following:
![register]https://user-images.githubusercontent.com/9632475/36400228-514c3c08-15d8-11e8-92b0-bb054cf6eca1.gif

# To do list




