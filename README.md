# LimeLight
_________________________________________________________
Version: 1.0

Author: Fuzz Productions

##Introduction
_________________________________________________________

LimeLight is a help authoring tool for Android applications. It creates interactable and context-driven help systems to guide users.

##Note
_________________________________________________________
LimeLight requires some level of configuration and is not a plug-in-play library. 

##How to use
_________________________________________________________

####Add to your application:
    
Include it in your project as a library and then modify the following methods in your main activity:

        @Override
        protected void onResume() {
            super.onResume();
            LimeLight.onResume(this);
        }

        @Override
        protected void onPause() {
            super.onPause();
            LimeLight.onPause();
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            LimeLight.onWindowFocusChanged(this, hasFocus);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            LimeLight.onCreateOptionsMenu(this, menu);
            return super.onCreateOptionsMenu(menu);
        }  
        
Run the application, and LimeLight's UI menu should appear on top.

#####For More Information
Refer to wiki at https://github.com/fuzz-productions/LimeLight/wiki

##License
	This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, as well as to the Additional Term regarding proper attribution. The latter is located in Term 11 of the License. If a copy of the MPL with the Additional Term was not distributed with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL