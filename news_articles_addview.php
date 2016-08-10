<?php
define("DB_HOST", "localhost");
define("DB_USER", "joshadmin1996");
define("DB_PASSWORD", "password1");
define("DB_DATABASE", "DB_NewsApp");

$con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

$article = $_POST['article'];

if(!$con){
    echo"conErr";
}
else{
	$insertQuery = mysqli_query($con,"INSERT INTO tblViews(article_viewed) VALUES ('".$article."')");
	if ($insertQuery)
	{
		echo"success";
	}
	else
	{
		echo"failure";
	}
}
  mysqli_close($con);
?> 
   