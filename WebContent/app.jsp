<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>IBM Distributed Marketing</title>

<script src="http://code.jquery.com/jquery-latest.js"></script>

    <!-- Bootstrap Core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    	<link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">
	<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>

    <!-- Theme CSS -->
    <link href="css/freelancer.min.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" type="text/css">
    
    
    <style>
.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

.switch input {display:none;}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}
</style>
    
    
    
    
    <script>
    
	var getUrlParameter = function getUrlParameter(sParam) {
        var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : sParameterName[1];
            }
        }
    };
    
    var password = getUrlParameter('password');
    var $password = $('.password');
    console.log("Token " + $password);
    var user = getUrlParameter('user');
    var $user = $('.user');
    console.log("User " + $user);
    
    var pdv = getUrlParameter('pdv');
    var $pdv = $('.pdv');
    console.log("pdv " + $pdv);
    var token;

	$(document).ready(function() {   
    	$.get('AuthServlet',{user:user,password:password},function(responseText) { 
    		console.log("Auth : " + responseText);
    		token= responseText;
    		$.get('Forms_campsub',{user:user,token:responseText,campaign_ID:pdv},function(responseText) { 
    			console.log("Response : " + responseText);
    			
				var list_form_attributes = JSON.parse(responseText);
        		
        		$.each(list_form_attributes, function (index, value) {
        			console.log(value.Info_Campaign); 
        			$(".formulaire").append("<div class=\"container-fluid\"><div class=\"row-fluid\"><h4><label><strong>"+value.Project_Name+"</strong> from "+value.Start_Date+" to "+value.End_Date+" </label></h4>");
        			
        			var value_button = "{\"PDV\": "+pdv+",\"PDV_Attribute_Name\": \""+value.PDV_Attribute_Name+"\",\"Project_ID\": "+value.Project_ID+",\"PDV_Attribute_ID\": "+value.PDV_Attribute_ID+",\"PDV_Attribute_Values\": \""+value.PDV_Attribute_Values+"\",\"Subscription\": "+value.Subscription+"}";
        			var nb_stores=value.PDV_Attribute_Values.split(",").length + 1;
        			console.log("Value_button " + value_button );
        			$(".formulaire").append("<h6><label>"+value.Campaign_Description+" Already <span class=\"badge\">" + nb_stores + " </span> registered stores.</label></h6>");
        			
        			if(value.PDV_Attribute_Values.includes(pdv))
        			{
        				$(".formulaire").append("<button type=\"button\" class=\"btn btn-danger disabled\" >Already subscribed</button>");
        			}
        			else
        			{
        				$(".formulaire").append("<button type=\"button\" class=\"btn btn-primary subscription\" value='"+value_button+"'>Subscribe</button>");
        			}
        			
        			$(".formulaire").append("</div></div>");      			
        			$(".formulaire").append("<br>");
        		});
              });
    	});
    });
	$(document).on("click", ".subscription", function () {
        console.log("Estimation");
        
        $button = $(this);
        console.log($(this).attr("value"));
        var values = JSON.parse($(this).attr("value"));
        $.get('Forms_Update',{user:user,token:token,campaign_ID:pdv,PDV_Attribute_Name:values.PDV_Attribute_Name,Project_ID:values.Project_ID,PDV_Attribute_ID:values.PDV_Attribute_ID,PDV_Attribute_Values:values.PDV_Attribute_Values,Subscription:values.Subscription},function(responseText) { 
        	console.log("Retour : " + responseText);
        	$(".formulaire").append("<br>");
        	$(".formulaire").append("<div class=\"alert alert-success\"><strong>Success!</strong>You are registered to this campaign.</div>");
        	$button.addClass("disabled");
        	$button.removeClass("btn-primary");
        	$button.addClass("btn-danger");
        	$button.text("Already subscribed");
        });

   });
	</script>
</head>

<body id="page-top" class="index">

    <!-- Navigation -->
    <nav id="mainNav" class="navbar navbar-default navbar-fixed-top navbar-custom">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header page-scroll">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span> Menu <i class="fa fa-bars"></i>
                </button>
                <a class="navbar-brand" href="#page-top">IBM Distributed Marketing</a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <li class="hidden">
                        <a href="#page-top"></a>
                    </li>
                    <li class="page-scroll">
                        <a href="#portfolio">List of available campaigns</a>
                    </li>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container-fluid -->
    </nav>

    <!-- Header -->
    <header>
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <img class="img-responsive" src="img/Distributed_Marketing_SkyBlue.png" alt="">
                    <div class="intro-text">
                        <span class="name">IBM Distributed Marketing Demo</span>
                        <hr class="star-light">
                    </div>
                </div>
            </div>
        </div>
    </header>

       <!-- Portfolio Grid Section -->
    <section id="portfolio">
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center">
                    <h2>List of Campaigns for subscription</h2>
                    <hr class="star-primary">
                </div>
            </div>
            <div class="panel panel-default ">
  				<div class="panel-body">
  				
  				
           		
  				<div class="row">
	             	<div class="col-lg-3 col-sm-3"></div>
	            	<div class="col-lg-6 col-sm-6 row formulaire text-center"></div>
	            	<div class="col-lg-3 col-sm-3"></div>
           		</div>
  				
  				<div class="row">
	             	<div class="col-lg-3 col-sm-3"></div>
	            	<div class="col-lg-6 col-sm-6 alert"></div>
	            	<div class="col-lg-3 col-sm-3"></div>
           		</div>
			</div>
			
        </div>
        </div>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
    </section>



    <!-- Footer -->
    <footer class="text-center">
               <div class="footer-below">
            <div class="container">
                <div class="row">
                    <div class="col-lg-12">
                        Copyright &copy; IBM 2016
                    </div>
                </div>
            </div>
        </div>
    </footer>

    <!-- Plugin JavaScript -->
    <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>

    <!-- Theme JavaScript -->
    <script src="js/freelancer.min.js"></script>

</body>

</html>
