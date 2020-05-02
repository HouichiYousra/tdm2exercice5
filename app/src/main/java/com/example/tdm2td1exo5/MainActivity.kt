package com.example.tdm2td1exo5;

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var button = findViewById<Button>(R.id.button)
        var msg = findViewById<EditText>(R.id.plain_text_input)
        var tel = findViewById<EditText>(R.id.plain_text_input3)
        loadContacts.setOnClickListener { loadContacts() }
        button.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                    Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.SEND_SMS),
                    PERMISSIONS_REQUEST_READ_CONTACTS)
                //callback onRequestPermissionsResult
            } else {
                val smgr: SmsManager = SmsManager.getDefault()
                smgr.sendTextMessage(tel.text.toString(), null, msg.text.toString(), null, null)
                Toast.makeText(applicationContext,"Message sent",Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun loadContacts() {
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS)
            //callback onRequestPermissionsResult
        } else {
            builder = getContacts()
            listContacts.text = builder.toString()

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(applicationContext,"Permission must be granted in order to display contacts information",Toast.LENGTH_LONG)
            }
        }
    }

    private fun getContacts(): StringBuilder {
        val builder = StringBuilder()
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null)

        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
/*                    val tel = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                            Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.SEND_SMS),
                            PERMISSIONS_REQUEST_READ_CONTACTS)
                        //callback onRequestPermissionsResult
                    } else {
                        val smgr: SmsManager = SmsManager.getDefault()
                        smgr.sendTextMessage(tel, null, findViewById<EditText>(R.id.plain_text_input).text.toString(), null, null)
                    }

*/
                    val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                    if (phoneNumber > 0) {
                        val cursorPhone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                        if (cursorPhone != null) {
                            if(cursorPhone.count > 0) {
                                while (cursorPhone.moveToNext()) {
                                    val phoneNumValue = cursorPhone.getString(
                                        cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    builder.append("Contact: ").append(name).append(", Phone Number: ").append(
                                        phoneNumValue).append("\n\n")
                                    Log.e("Name ===>",phoneNumValue);
                                }
                            }
                        }
                        if (cursorPhone != null) {
                            cursorPhone.close()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext,"No contacts available!",Toast.LENGTH_SHORT)
            }
        }
        if (cursor != null) {
            cursor.close()
        }
        return builder
    }
}