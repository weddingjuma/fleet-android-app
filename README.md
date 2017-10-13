# **Mapotempo Fleet Android **
----------
Mapotempo Fleet Android is an application which respond to the basic needs of delivery management.
The Application's structure is fragments composed, through 3 principal views : 
  1. Login 
  2. Missions List
  3. Mission Detail

### **Login Fragment**

> **Description:**
> Responsible of establish a connection with the library that allow you to get access to Mapotempo models. The fragment launch a connection attempt that give you, in some cases, a manager. The manager allow you to get access to the following entities : 
> - Missions
> -  User
> - Misison Status
> - Company

#### **Integration**
As a Fragment you must implement it in your XML file through the following line of code : 
 ```<fragment class="mapotempo.com.mapotempo_fleet_android.login.LoginFragment" /> ```

This fragment require the implementation of  ``` public interface OnLoginFragmentImplementation {} ``` directly in the Activity that hold the Login Fragment. 

You will have to implement the `public void onLoginFragmentImplementation(MapotempoFleetManagerInterface.OnServerConnexionVerify.Status status, TimerTask task, String[] logs)` wich will be called by an async task. If the library doesn't respond then, a timeout will stop the attempt and give the user back to the login page.

As you'ill need to use the manager during the whole life cycle of the application, we highly recommend to keep a reference to it in a descendent of Application.

**Exemple**: 
```
public void onLoginFragmentImplementation(MapotempoFleetManagerInterface.OnServerConnexionVerify.Status status, TimerTask task, String[] logs) {

task.cancel();
        switch (status) {
            case VERIFY:
                if (logs != null)
                    keepTraceOfConnectionLogsData(logs);
                break;
            case USER_ERROR:
            case PASSWORD_ERROR:
                LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.hook_login_fragment);
                loginFragment.toogleLogginView(false);
                break;
            default:
                onBackPressed();
                break;
        }
 }
```
 
### **Mission List Fragment**

> **Description:**
> This fragment is responsible of display all missions assigned to the user currently connected.
> Each view displayed by this fragment is composed of 3 main UI elements : 
> - The name 
> - The delivery date
> - The delivery hour

#### **Integration**
First and foremost, it is needed to implement the fragment through XML using the following class : 
```
<fragment class="mapotempo.com.mapotempo_fleet_android.mission.MissionsFragment" />
```
This fragment require the implementation of  ``` public interface OnMissionsInteractionListener {} ``` directly in the Activity that hold the List Fragment. 

Then Override the ``` View.OnClickListener onListMissionsInteraction(int position); ``` which force to return an Android's click Listener. If you don't know about Event Listeners please check the documentation here : [Android Click Listener.](https://developer.android.com/reference/android/view/View.OnClickListener.html)
Feel free to put any logic inside the listener. Keep in mind that the position returned can be used to get the detailed view of the mission triggered by a click.

**Here is an example of usability** : 
```
    @Override
    public View.OnClickListener onListMissionsInteraction(final int position) {
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	                intent = new Intent(v.getContext(), YouNewActivity.class);
	                intent.putExtra("mission_position", position);
				    v.getContext().startActivity(intent);
                }
            }
        };
        return onClick;
    }
```

### **Mission Detail**
> **Description:**
> This fragment is a view detailed of a mission. it is working along side a [ViewPager](https://developer.android.com/reference/android/support/v4/view/ViewPager.html)  that allow users to swipe left/right side to get the previous/next mission's view. Moreover this fragment provide the following functionalities : 
> - <i class="icon-trash"></i> **Delete Mission**: Simply tag the current mission as deleted.
> - <i class="icon-pencil"></i> **Update Status**: Change the Status of the current mission. Status are mostly custom, directly pull from    database.
> - <i class="icon-refresh"></i> **Go to Location**: An [Intent](https://developer.android.com/reference/android/content/Intent.html) wich give the possibility to open a maps application in order to view the mission's géolocalisation from its lat/lng coordinates. 

#### **Integration**
As it is needed for all fragment, you must implement it inside your XML file using the following lines of code : 
```
<fragment class="mapotempo.com.mapotempo_fleet_android.mission.MissionFragment"
        android:id="@+id/base_fragment"
        app:ViewStyle="SCROLLVIEW"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
It is Highly recommended to set up the enum "ViewStyle" to "SCROLLVIEW" in order to benefit of full features. 

This fragment require the implementation of  ``` public interface OnFragmentInteractionListener { ... } ``` directly in the Activity that hold the Detail Fragment. 
This involve the override of ``` onSingleMissionInteraction(Mission mission) ```. This interface is called when a modification has been done to a mission. 

As we are using a [RecyclerView](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html) to manage the list. you shall notify the changement to this fragment through this method. **Exemple**: 
```
    @Override
    public void onSingleMissionInteraction(Mission mission) {
        MissionsFragment missionsFragment = (MissionsFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);

        if (missionsFragment != null)
            missionsFragment.recyclerView.getAdapter().notifyDataSetChanged();

		/* Do here whatever you need with the Mission object */
    }
```

