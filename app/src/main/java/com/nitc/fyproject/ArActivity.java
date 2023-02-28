package com.nitc.fyproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArActivity extends AppCompatActivity {
    FloatingActionButton ArButton;

    ArFragment arFragment;
    ModelRenderable modelRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);


        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        sceneViewerIntent.setData(Uri.parse("https://arvr.google.com/scene-viewer/1.0?file=https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf"));
        sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
        startActivity(sceneViewerIntent);

//        Intent intent = getIntent();
//        String obj = intent.getStringExtra("Name");
//
//        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
//
//        ArButton = findViewById(R.id.ArButton);
//        ArButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(ArActivity.this, MainActivity.class));
//            }
//        });
//
//        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
//
//        modelRenderable = new MainActivity().modelRenderable;
//        // Render the model if it is not null
//        if (modelRenderable != null) {
//            Frame frame = arFragment.getArSceneView().getArFrame();
//            if (frame != null) {
//                Pose pose = frame.getCamera().getPose().compose(Pose.makeTranslation(0, 0, -1f));
//                Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pose);
//                createModel(anchor);
//            }
//        }
//        createModel();
//        setUpModel();

    }

    private void createModel(Anchor anchor) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        transformableNode.select();
    }

    private void setUpModel() {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("apple.glb"))
                .build()
                .thenAccept(renderable -> {
                    modelRenderable = renderable;
                    Frame frame = arFragment.getArSceneView().getArFrame();
                    if (frame != null) {
                        Pose pose = frame.getCamera().getPose().compose(Pose.makeTranslation(0, 0, -1f));
                        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pose);
                        createModel(anchor);
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Can't Load", Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

}