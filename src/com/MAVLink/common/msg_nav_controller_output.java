/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE NAV_CONTROLLER_OUTPUT PACKING
package com.MAVLink.common;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPayload;
        
/**
* The state of the fixed wing navigation and position controller.
*/
public class msg_nav_controller_output extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_NAV_CONTROLLER_OUTPUT = 62;
    public static final int MAVLINK_MSG_LENGTH = 26;
    private static final long serialVersionUID = MAVLINK_MSG_ID_NAV_CONTROLLER_OUTPUT;


      
    /**
    * Current desired roll in degrees
    */
    public float nav_roll;
      
    /**
    * Current desired pitch in degrees
    */
    public float nav_pitch;
      
    /**
    * Current altitude error in meters
    */
    public float alt_error;
      
    /**
    * Current airspeed error in meters/second
    */
    public float aspd_error;
      
    /**
    * Current crosstrack error on x-y plane in meters
    */
    public float xtrack_error;
      
    /**
    * Current desired heading in degrees
    */
    public short nav_bearing;
      
    /**
    * Bearing to current waypoint/target in degrees
    */
    public short target_bearing;
      
    /**
    * Distance to active waypoint in meters
    */
    public int wp_dist;
    

    /**
    * Generates the payload for a mavlink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_NAV_CONTROLLER_OUTPUT;
              
        packet.payload.putFloat(nav_roll);
              
        packet.payload.putFloat(nav_pitch);
              
        packet.payload.putFloat(alt_error);
              
        packet.payload.putFloat(aspd_error);
              
        packet.payload.putFloat(xtrack_error);
              
        packet.payload.putShort(nav_bearing);
              
        packet.payload.putShort(target_bearing);
              
        packet.payload.putUnsignedShort(wp_dist);
        
        return packet;
    }

    /**
    * Decode a nav_controller_output message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.nav_roll = payload.getFloat();
              
        this.nav_pitch = payload.getFloat();
              
        this.alt_error = payload.getFloat();
              
        this.aspd_error = payload.getFloat();
              
        this.xtrack_error = payload.getFloat();
              
        this.nav_bearing = payload.getShort();
              
        this.target_bearing = payload.getShort();
              
        this.wp_dist = payload.getUnsignedShort();
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_nav_controller_output(){
        msgid = MAVLINK_MSG_ID_NAV_CONTROLLER_OUTPUT;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a mavlink packet
    *
    */
    public msg_nav_controller_output(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_NAV_CONTROLLER_OUTPUT;
        unpack(mavLinkPacket.payload);        
    }

                    
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_NAV_CONTROLLER_OUTPUT - sysid:"+sysid+" compid:"+compid+" nav_roll:"+nav_roll+" nav_pitch:"+nav_pitch+" alt_error:"+alt_error+" aspd_error:"+aspd_error+" xtrack_error:"+xtrack_error+" nav_bearing:"+nav_bearing+" target_bearing:"+target_bearing+" wp_dist:"+wp_dist+"";
    }
}
        