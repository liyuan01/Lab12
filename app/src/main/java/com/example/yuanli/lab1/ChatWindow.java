package com.example.yuanli.lab1;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.yuanli.lab1.R.styleable.View;

public class ChatWindow extends AppCompatActivity {
     ListView listViewM;
     EditText inputMessage;
     Button SendButton;


    ArrayList<String> chatMessages;

    ChatAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        listViewM=(ListView) findViewById(R.id.Lv);
        inputMessage=(EditText)findViewById(R.id.textMessage);
        SendButton=(Button)findViewById(R.id.sbutton);

        chatMessages=new ArrayList<>();

        //in this case, “this” is the ChatWindow, which is-A Context object
        messageAdapter =new ChatAdapter( this );
        listViewM.setAdapter ( messageAdapter);
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chatMessages.add(inputMessage.getText().toString()) ;

                messageAdapter.notifyDataSetChanged(); //this restarts the process of getCount()/ getView()

                inputMessage.setText("");
            }
        });

    }

    class ChatAdapter extends ArrayAdapter<String> {
        public ChatAdapter(Context ctx) {
            super(ctx, 0);
        }
        public int getCount(){
            Log.i("Size::", Integer.toString(chatMessages.size()));

            return chatMessages.size();


        }
        public String getItem(int position){
            return (String) chatMessages.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){

            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null ;

            if(position%2 == 0)
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            else
                result = inflater.inflate(R.layout.chat_row_incoming, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText( getItem(position)  ); // get the string at position
            return result;

        }

    }
}
