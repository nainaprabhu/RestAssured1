import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.GistService;
 
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.testng.annotations.Test;
import org.testng.Assert;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.eclipse.egit.github.core.*; 
 
public class APITest {
 
	@Test  (priority=1)
	public void CreateGistTest()
	{
		 try {
			 //Create A New Gist:
	            GitHubClient gitHubClient = new GitHubClient();
	            gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	            //gitHubClient.setOAuth2Token("2d3876d33ce883fe1daeeaf6e9a48de10f7f7cbd");
	            Gist gist = new Gist().setDescription("Prints a string to standard out...");
	            GistFile file = new GistFile().setContent("System.out.println(\"Hello World\");");
	            gist.setFiles(Collections.singletonMap("Hello.java", file));
	            //gist.setId("RestAssuredAPITestingPayconiqTest");
	            gist = new GistService(gitHubClient).createGist(gist);
	            
	            RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
				RequestSpecification httpRequest = RestAssured.given();
				Response response = httpRequest.request(Method.GET, "/");
				String responseBody = response.getBody().asString();
				//System.out.println("Response Body is =>  " + responseBody);
				Assert.assertNotEquals(responseBody, "Not Found", "Gist is not created properly, please check!!");
				Assert.assertEquals(response.getStatusCode(), 200);
				//Need to add more check TO-DO
			 	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	@Test  (priority=2)
	public void ReadGistTest() {
		try {
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                		                   
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				  Assert.assertNotEquals(responseBody, "Not Found", "Gist is not fetched properly, please check!!");
	                      Assert.assertEquals(gist1.getDescription(), "Prints a string to standard out...");
	                      Assert.assertEquals(gist1.getComments(), 0);
	                    
	                  }
	                }
	            }
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@Test  (priority=3)
	public void ValidateGistContentTest() {
		try {
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                		                   
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				  Assert.assertNotEquals(responseBody, "Not Found", "Gist is not fetched properly, please check!!");
	    				  Assert.assertEquals(response.getStatusCode(), 200);
	                    
	                    Map<String, GistFile> stringFileMap = gist1.getFiles();
	                    stringFileMap.forEach((s, f) -> {	                        		                        
	                        HttpURLConnection urlConnection = null;
	 
	                        try {
	                            urlConnection =
	                                    (HttpURLConnection) new URL(f.getRawUrl()).openConnection();
	 
	                            String result = getStringFromInputStream(urlConnection.getInputStream());
	                            //System.out.println("out:\n" + result);
	                            Assert.assertEquals(result, "System.out.println(\"Hello World\");");
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        } finally {
	                            if (urlConnection != null) {
	                                urlConnection.disconnect();
	                            }
	                        }
	 
	 
	                    });
	                  }
	                }
	            }
		} catch (Exception e) {
            e.printStackTrace();
        }
	}

	
	
	@Test  (priority=4)
	public void CreateCommentGistTest()
	{
		try {
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                	
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				  Assert.assertEquals(response.getStatusCode(), 200);
	    				  //Update comments for gist1 created in create test:
	    				  GistService gs = new GistService(gitHubClient);
	    				  gs.createComment(gist1.getId(),"Adding comment for testing update");		    				  
	    				    
						  }
	                }               
	           
	            }
	            
	      //Now assert that comment got added
          Gist ourgist1 = getOurGist();
          Assert.assertEquals(ourgist1.getComments(), 1);	         	           
		} catch (Exception e) {
         e.printStackTrace();
     }
	}
	
    @Test  (priority=5)
	public void ForkGistTest()
	{
		try {
			String gistid = "";
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         //gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
			 gitHubClient.setOAuth2Token("2d3876d33ce883fe1daeeaf6e9a48de10f7f7cbd");
			 gistid = getGistId();
			 Assert.assertNotNull(gistid, "Fork script could not find gist id, please check");
	                      RestAssured.baseURI = "https://api.github.com/users/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gistid );
	    				  String responseBody = response.getBody().asString();	    				  
	    				  GistService gs = new GistService(gitHubClient);
	    				  gs.forkGist(gistid);
	    				//how to check fork:       GET /gists/:gist_id/forks
	    				  response = httpRequest.request(Method.GET, "/gists/:" + gistid + "/forks" );
	    				  responseBody = response.getBody().asString();	    				  	    				  
	    				  Assert.assertNotEquals(responseBody, "Not Found", "Gist is not forked properly, please check!!");
	                
		} catch (Exception e) {
         e.printStackTrace();
     }
	}
	 
    @Test  (priority=7)
	public void StarGistTest()
	{
		try {
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                	
	                	
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				  //Update comments for gist1 created in create test:
	    				      				  
	    				  GistService gs = new GistService(gitHubClient);
	    				  gs.starGist(gist1.getId());
	    				  //check if gist got starred
	    				  response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  responseBody = response.getBody().asString();	    				  
	    				  boolean isstar = gs.isStarred(gist1.getId());
	    				  Assert.assertEquals(response.getStatusCode(), 200);
	    				 Assert.assertEquals(isstar, true);
	    				 Assert.assertNotEquals(responseBody, "Not Found", "Gist is not fetched properly, please check!!");
	                    
	                  }
	                }
	            }
		} catch (Exception e) {
         e.printStackTrace();
     }
	}
	
	@Test  (priority=8)
	public void UnStarGistTest()
	{
		try {
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                	
	                    
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();	    				  
	    				  //Update comments for gist1 created in create test:
	    				  Assert.assertEquals(response.getStatusCode(), 200);
	    				  GistService gs = new GistService(gitHubClient);
	    				  gs.unstarGist(gist1.getId());
	    				  //check that gist got unstarred
	    				  boolean isstar = gs.isStarred(gist1.getId());
		    			  Assert.assertEquals(isstar, false);
	    				  Assert.assertNotEquals(responseBody, "Not Found", "Gist is not fetched properly, please check!!");	                   
	                  }
	                }
	            }
		} catch (Exception e) {
         e.printStackTrace();
     }
	}
	
	//@Test  (priority=9)
	public void PublicGistTest()
	{
		try {
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                	
	                	
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				  //Update comments for gist1 created in create test:
	    				  gist1.setPublic(true);    				  
	    				  GistService gs = new GistService(gitHubClient);
	    				  gs.updateGist(gist1);
	    				  //response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  //responseBody = response.getBody().asString();
	    			      //Now assert that comment got added
	    		          Gist ourgist1 = getOurGist();
	    		          boolean ispub = ourgist1.isPublic();
	    				  System.out.println("Set Public:" + ispub);
	    				  Assert.assertEquals(ispub, true);
	    				  Assert.assertNotEquals(responseBody, "Not Found", "Gist is not fetched properly in make public test, please check!!");	                    
	                  }
	                }
	            }
		} catch (Exception e) {
         e.printStackTrace();
     }
	}
		
	//@Test  (priority=10)
	public void EditCommentGistTest()
	{
		try {
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                	
	                	
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  httpRequest.body("Changing comment to check that edit works for testing api");
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				 // System.out.println("Response Body in Read is =>  " + responseBody);
	    				  //Update comments for gist1 created in create test:
	    				      				  
	    				  GistService gs = new GistService(gitHubClient);
	    				 // gs.createComment(gist1.getId(),"Adding comment for testing update");	
	    				  List<Comment> c = gs.getComments(gist1.getId());
	    				  for (Comment temp : c) {
	    					  System.out.println(temp.getBody());
	    				      long id = temp.getId();	    				      
	    				      temp.setBody("Changing comment to check that edit works for testing api");
	    				      //gs.editComment(temp);
	    				      
	    				      response = httpRequest.request(Method.PATCH, "/gists/:" + gist1.getId() + "/comments/:" + temp.getId() );
		    				  responseBody = response.getBody().asString();
		    				  Assert.assertEquals(response.getStatusCode(), 200);
		    				  System.out.println("STatus code: " + response.getStatusCode());		    				  
	    				  }	    				  	    				     				  	                   
	                  }
	                }
	            }
	            
	  	      //Now assert that comment got edited
	            Gist ourgist1 = getOurGist();
	            GistService gs = new GistService(gitHubClient);
	            List<Comment> c = gs.getComments(ourgist1.getId());
				  for (Comment temp : c) {
				      Assert.assertEquals(temp.getBody(),"Changing comment to check that edit works for testing api");
    				  Assert.assertNotEquals(ourgist1.getUpdatedAt(), ourgist1.getCreatedAt());
				  }	            
	            //Assert.assertEquals(gist1.getComments(), 1);
		} catch (Exception e) {
         e.printStackTrace();
     }
	}
	
   // @Test  (priority=11)
	public void DeleteCommentGistTest()
	{
		try {
		       //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                	
	                	
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				 // System.out.println("Response Body in Read is =>  " + responseBody);
	    				  //Update comments for gist1 created in create test:
	    				      				  
	    				  GistService gs = new GistService(gitHubClient);
	    				  //gs.createComment(gist1.getId(),"Adding comment for testing update");	
	    				  //check if comment count increased to 1	    				  
	    				  gs.deleteComment(1);
	    				  //check comment got deleted
	    				  Assert.assertNotEquals(gist1.getUpdatedAt(), gist1.getCreatedAt());
	    				  
	    				  Assert.assertNotEquals(responseBody, "Not Found", "Gist is not fetched properly, please check!!");
	                    

	                  }
	                }
	            }
		} catch (Exception e) {
         e.printStackTrace();
     }
	}
	
	
	@Test  (priority=12)
	public void DeleteGistTest()
	{
		try {
			//Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         Gist gistToDelete = null;
	         String idDeleted = "";
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                	
	                  
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				  //System.out.println("Response Body is =>  " + responseBody);
	                      Assert.assertNotEquals(responseBody, "Not Found", "Gist is not fetched properly in delete, please check!!");
	                      gistToDelete=gist1;
	                      idDeleted = gist1.getId();
	                      
	                  }
	                }
	            }
				GistService gs = new GistService(gitHubClient);
				gs.deleteGist(idDeleted);
					//Now validate the deletion
					RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
   				    RequestSpecification httpRequest = RestAssured.given();
   				    Response response = httpRequest.request(Method.GET, "/" + idDeleted );
   				    String responseBody = response.getBody().asString();
   				    //System.out.println("Response Body is =>  " + responseBody);
                    Assert.assertEquals(responseBody, "Not Found", "Gist is not deleted properly, please check!!");
	            
		} catch (Exception e) {
            e.printStackTrace();
        }
		}
	
	  private static String getStringFromInputStream(InputStream is) {
	        StringBuilder stringBuilder = new StringBuilder();
	        Scanner scanner = new Scanner(is, "UTF-8");
	        while (scanner.hasNextLine()) {
	            stringBuilder.append(scanner.nextLine());
	            stringBuilder.append("\n");
	        }
	 
	        return stringBuilder.toString().trim();
	    }
 
	  
		private String getGistId()
		{
			String gistid = null;
			try {
			       //Read from created Gist
				 GitHubClient gitHubClient = new GitHubClient();
		         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
		         
		         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
		            while (gistPageIterator.hasNext()) {
		                Collection<Gist> gistCollection = gistPageIterator.next();
		 
		                for (Gist gist1 : gistCollection) {	                	
		                	
		                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
		                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
		    				  RequestSpecification httpRequest = RestAssured.given();
		    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
		    				  String responseBody = response.getBody().asString();
		    				  //Update comments for gist1 created in create test:
		    				     gistid = gist1.getId(); 
		    				     GistService gs = new GistService(gitHubClient);
		    				  //String user = gist1.getUser().getEmail();
		    				 // Assert.assertNotEquals(responseBody, "Not Found", "Gist is not fetched properly, please check!!");
		                     
		                  }
		                }
		            }
			} catch (Exception e) {
	         e.printStackTrace();
	     }
			//System.out.println("gist2: "+ gistid);
		 return gistid;
		}
   
		
  private Gist getOurGist() 
   {	
	   Gist ourgist1 = null;
	   try {		   
		     //Read from created Gist
			 GitHubClient gitHubClient = new GitHubClient();
	         gitHubClient.setCredentials("nainaprabhu", "pulsar5686");
	         
	         PageIterator<Gist> gistPageIterator = new GistService(gitHubClient).pageGists("nainaprabhu");  
	            while (gistPageIterator.hasNext()) {
	                Collection<Gist> gistCollection = gistPageIterator.next();
	 
	                for (Gist gist1 : gistCollection) {	                	
	                    if(gist1.getDescription().equals("Prints a string to standard out..."))  {
	                      RestAssured.baseURI = "https://gist.github.com/nainaprabhu";
	    				  RequestSpecification httpRequest = RestAssured.given();
	    				  Response response = httpRequest.request(Method.GET, "/" + gist1.getId() );
	    				  String responseBody = response.getBody().asString();
	    				 ourgist1 = gist1; 
	    				}
	                }               
	           
	            }	           
		} catch (Exception e) {
      e.printStackTrace();
  }
	   return ourgist1;
   }
}