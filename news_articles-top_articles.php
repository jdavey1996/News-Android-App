<?php
	define("DB_HOST", "localhost");
	define("DB_USER", "joshadmin1996");
	define("DB_PASSWORD", "password1");
	define("DB_DATABASE", "DB_NewsApp");

	$con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

	$sql = mysqli_query($con,"SELECT article_viewed FROM tblViews GROUP BY article_viewed Order by COUNT(*) DESC, article_viewed DESC LIMIT 5");
		
	$order = array();

	while($row = $sql->fetch_assoc()){
		$order[] = $row;
	}


	$str = file_get_contents('http://josh-davey.com/news_app_data/news_articles-all.json');

	$json = json_decode($str, true);
	
	$data = array();
	foreach($order as $ord){
		foreach($json['articles'] as $article){
			if ($article['number'] == $ord["article_viewed"])
			{	
				$data[]= $article;
			}
		}	
	}
	echo json_encode($data);

  mysqli_close($con);
?> 
   