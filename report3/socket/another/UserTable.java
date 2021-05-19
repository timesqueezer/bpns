package javaLanguage.basic.socket.another;

/**
 * a synchronized map of users keyed by name
 * provides a rename method, other methods are overloaded, not overridden
 * @author PKWooster
 * @version 1.0 June 15,2004
 */
public class UserTable extends java.util.TreeMap
{
        public synchronized User get(String name){return (User)super.get(name);}
 
        public synchronized boolean add(String name, User user)
        {
                if(containsKey(name))return false;      // don't reuse
                {
                        put(name, user);
                        return true;
                }
        }
 
        public synchronized void delete(String name){remove(name);}
 
        public synchronized boolean rename(String oldName, String newName)
        {
                if(oldName.equals(newName))return true;
                User u = get(oldName);
                if(containsKey(newName) || u == null)return false;
                else
                {
                        remove(oldName);
                        add(newName,u);
                }
                return true;
        }
        
        public synchronized User[] allUsers()
        {
                Object [] ov = this.values().toArray();
                User [] uv = new User[ov.length];
                for(int i=0; i<ov.length;i++)uv[i]=(User)ov[i];
                return uv;
        }
}