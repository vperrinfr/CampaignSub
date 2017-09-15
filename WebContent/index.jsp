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
    var campaign_ID = getUrlParameter('campaign_ID');
    var $campaign_ID = $('.campaign_ID');
    console.log("campaign_ID " + $campaign_ID);
   var token;
   var email_template_id;
    var id_form =123;
	$(document).ready(function() {   
    	$.get('AuthServlet',{user:user,password:password},function(responseText) { 
    		console.log("Auth : " + responseText);
    		token= responseText;
    		$.get('Forms_retrieve',{user:user,token:responseText,campaign_ID:campaign_ID},function(responseText) { 
    			console.log("Response : " + responseText);
    			id_form = responseText.split("/")[0]; 
        		var form_values = responseText.split("/")[1];
        		var list_form_attributes = JSON.parse(form_values);
        		
        		$.each(list_form_attributes, function (index, value) {
        			console.log(value.Display_Name); 
        			if((value.Attribute_Type=="text"))
        				{
	        				$(".email").append("Email Template ID is : " + value.Default_Value_For_Options);
							email_template_id = value.Default_Value_For_Options;
							$.get('IMC_preview',{mailing_ID:email_template_id},function(responseText) { 
					    		$('#previewhtml').html(responseText);  
					    		code_html = responseText;
					        });
        				}
        			
        			if((value.Attribute_Type=="radiogroup")||(value.Attribute_Type=="select"))
        				{
        				
        				var string = value.Display_Name,
        			    substring = "Canal";
        				var table_options = value.Lookup_Options;    				
        				
        				if(string.indexOf(substring) == -1){
        				$(".formulaire").append("<div><label class=\"control-label\">"+value.Display_Name+"</label>");
        				$(".formulaire").append("<select class=\"form-control\" name=\""+value.Internal_Name+"\" id=\""+value.Internal_Name+"\">");
        				$(".formulaire").append("</select>");
        				$(".formulaire").append("</div>");
        				
        				$.each(table_options, function (index, value1) {
        					$("#"+value.Internal_Name).append("<option>"+table_options[index]+"</option>");
        				});
        				}
						else{
							$(".formulaire_canaux").append(value.Display_Name + " <label class=\"switch\"><input type=\"checkbox\" name=\""+value.Internal_Name+"\" id=\""+value.Internal_Name+"\"  > <div class=\"slider round\"></div></label> ");
        				}
        				}
        				}); //for each		
				$(".formulaire").append("<br>");
        		$(".formulaire").append("<div class=\"form-group\" ><button id=\"estimation\" name=\"estimation\" class=\"btn btn-primary estimation\">Target Estimation</button></div>");
        		$(".formulaire").append("<div id=\"resultat\" style=\"display:none\">  First estimation of the target: <span id=\"estimation_value\"><img src=\"img/ajax-loader.gif\"></span></div>");
                
                $(".formulaire_canaux").append("<br>");
                $(".formulaire_canaux").append("<br>");
        		$(".formulaire_canaux").append("<div class=\"form-group\"><button id=\"estimation\" name=\"estimation\" class=\"btn btn-primary channel\" style=\"display:none\">Target Estimation per channel</button></div>");
        		$(".formulaire_canaux").append("<div id=\"resultat_channel\" style=\"display:none\">  First estimation per channels : ");
        		$(".formulaire_canaux").append("<span id=\"estimation_value_channel\" style=\"display:none\"><img src=\"img/ajax-loader.gif\"></span>");
        		$(".formulaire_canaux").append("</div>");
        		
        		$(".formulaire_exec").append("<br>");
        		$(".formulaire_exec").append("<div class=\"form-group\"><button id=\"execution\" name=\"execution\" class=\"btn btn-primary execution\" style=\"display:none\">Execution of the campaign</button></div>");
        		$(".formulaire_exec").append("</div>");
        		
              });
    	});
    });
	$(document).on("click", ".estimation", function () {
        console.log("Estimation");
        
        $( "form" ).on( "submit", function( event ) {
        	  event.preventDefault();
        	  var values = $( this ).serialize();
        	  $('input[type=checkbox]').each(function() {     
        		    if (!this.checked) {
        		    	values += '&'+this.name+'=off';
        		    }
        		});
        	  
        	  console.log( "values" + values );
        	  $("#resultat").show();        	 
              $("#estimation_value_channel").show();
              $("#estimation_value_channel").html("<img src=\"img/ajax-loader.gif\">");
              $("#estimation_value").html("<img src=\"img/ajax-loader.gif\">");
        	  $.get('Forms_publish',{user:user,token:token,campaign_ID:campaign_ID,id_form:id_form,values:values},function(responseText) {
        	console.log( "Click Estimation" + responseText );
        	if (responseText=="NotGood") 
      			{
      			 $('#estimation_value').html("<div class=\"alert alert-danger\" role=\"alert\"><span class=\"glyphicon glyphicon-exclamation-sign\" aria-hidden=\"true\"></span>Problème dans le traitement de votre campagne</div>");
      			}
      		else
      		{
      			var json_body = JSON.parse(responseText);
      			
      		 	var json_body2 = json_body.Output_Variables;
      		 	console.log( "json_body2 " + json_body2 );
      		 	$("#estimation_value_channel").html("");
      		 	for(var obj in json_body2){
      		 		var temp=json_body2[obj].Attribute_Label;
      		 		if(temp.indexOf("Canal")!==-1){
      		 	    	$("#estimation_value_channel").append(json_body2[obj].Attribute_Label + " : <strong>" + json_body2[obj].Attribute_Values+"</strong>");
      		 		$("#estimation_value_channel").append("<br>");
      		 		}
      		 		if(temp.indexOf("Age")!==-1){
      		 			$("#estimation_value").html("<strong>"+json_body2[obj].Attribute_Values+"</strong>")
      		 		}
      		 	}
      		 	$("#estimation_value_channel").append("New estimation with channel optout : <strong>"+json_body.Count+"</strong>");
      		 	$(".channel").show();
      		 	$(".execution").show();
      		}  
        	  });
        	}); 

   });
	
	$(document).on("click", ".execution", function () {
        console.log("execution");
        
        $.get('Forms_exec',{user:user,token:token,campaign_ID:campaign_ID},function(responseText) {
        	
        	if (responseText=="NotGood") 
  			{
  			 $('.formulaire_exec').html("<div class=\"alert alert-danger\" role=\"alert\"><span class=\"glyphicon glyphicon-exclamation-sign\" aria-hidden=\"true\"></span>Problem with your campaign.</div>");
  			}
  		else
  		{
        	 $('.formulaire_exec').html("<div class=\"alert alert-success\" role=\"alert\"><span class=\"glyphicon glyphicon-info-sign\" aria-hidden=\"true\"></span>Your campaign has been sent to your customers.</div>");
  		}	
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
                        <a href="#portfolio">Selection</a>
                    </li>
                    <li class="page-scroll">
                        <a href="#about">Channels</a>
                    </li>
                    <li class="page-scroll">
                        <a href="#email">Email Template</a>
                    </li>
                    <li class="page-scroll">
                        <a href="#validation">Validation</a>
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
                        <span class="skills">Vincent Perrin</span>
                    </div>
                </div>
            </div>
        </div>
    </header>
<form>
    <!-- Portfolio Grid Section -->
    <section id="portfolio">
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center">
                    <h2>Selection</h2>
                    <hr class="star-primary">
                </div>
            </div>
             <div class="row">
             	<div class="col-lg-3 col-sm-3"></div>
            	<div class="col-lg-6 col-sm-6 row formulaire text-center"></div>
            	<div class="col-lg-3 col-sm-3"></div>
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
    
    <!-- Portfolio Grid Section -->
    <section id="about">
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center">
                    <h2>Channel Selection</h2>
                    <hr class="star-primary">
                </div>
            </div>
            <div class="row formulaire_canaux text-center">
	 		
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
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
    </section>
     </form>  
     
      <!-- Portfolio Grid Section -->
    <section id="email">
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center">
                    <h2>eMail Template</h2>
                    <hr class="star-primary">
                </div>
            </div>
            <div class="row email text-center">
            </div>
            
            <div class="row">
             	<div class="col-lg-5 col-sm-3"></div>
            	<div class="col-lg-4 col-sm-6"><button type="button" class="btn btn-info" data-toggle="collapse" data-target="#demo">Display Email Template</button></div>
            	<div class="col-lg-3 col-sm-3"></div>
            </div>
            
            <div class="panel panel-default collapse" id="demo">
  				<div class="panel-body"><div id="previewhtml" ></div></div>
			</div>
           
            
        </div>
    </section> 
 
      <!-- Portfolio Grid Section -->
    <section id="validation">
        <div class="container">
            <div class="row">
                <div class="col-lg-12 text-center">
                    <h2>Campaign Execution</h2>
                    <hr class="star-primary">
                </div>
            </div>
            <div class="row formulaire_exec text-center">
            </div>
        </div>
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
