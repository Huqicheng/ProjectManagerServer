package utils;

import java.net.*;
import java.util.*;
import com.google.gson.Gson;
public class PersistentCookieStore implements CookieStore, Runnable {
    CookieStore store;

    public PersistentCookieStore() {
        // get the default in memory cookie store
        store = new CookieManager().getCookieStore();
        
        // todo: read in cookies from persistant storage
        // and add them store
        
        // this.add(url, cookie)

        // add a shutdown hook to write out the in memory cookies
        Runtime.getRuntime().addShutdownHook(new Thread(this)); 
    }

    public void run() {
        // todo: write cookies in store to persistent storage
    	 
    	//1. to json string
    	try {
			String jsonCookies = new Gson().toJson(this.store.get(new URI("192.168.11.9/ProjectManager/")));
			System.out.print(jsonCookies);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//2. preserve json string to file through FileStirng
    }

    public void add(URI uri, HttpCookie cookie) {
        store.add(uri, cookie);
    }

    public List<HttpCookie> get(URI uri) {
        return store.get(uri);
    }

    public List<HttpCookie> getCookies() {
        return store.getCookies();
    }

    public List<URI> getURIs() {
        return store.getURIs();
    }

    public boolean remove(URI uri, HttpCookie cookie) {
        return store.remove(uri, cookie);
    }

    public boolean removeAll()  {
        return store.removeAll();
    }
    
    public static void main(String[] args) throws URISyntaxException {
    	PersistentCookieStore ps = new PersistentCookieStore();
    	ps.add(new URI("192.168.11.9/ProjectManager/"), new HttpCookie("223", "223"));
    	
    	//System.out.print(ps.get(new URI("192.168.11.9/ProjectManager/")));
	}
}
