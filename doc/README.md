# Application Model

    - Scenes
        |- Scene
            |- Object3DData         <list>
                | - Parent Node
            |- Root Joints          <list>
            |- Cameras              <list>
            |- Animations           <list>
            |- Skins                <lisT


# GLTF Model to App Model - Mesh details

    - Mesh                  --------->  List<Object3DData> 
        |- Primitive            ----->      |- Obejct3DData <fully assembled>
                |- POSITION                     |- Vertices                         <unrolled>
                |- NORMAL                       |- Normals                          <unrolled>
                |- INDICES                      |- Indices                          <unrolled>
                |- TANGENT                      |- Tangents                         <unrolled>
                |- TEXCOORD_0                   |- TexCoords                        <unrolled>
                |- COLOR_0                      |- Colors                           <unrolled>
                |- JOINTS_0                     |
                |- WEIGHTS_0                    |
                                                |
    - Skin                                      |- Skin                             <clone>
        |- Root Joint             --->          |   |- Root Joint                   <shallow>
        |- JointsIds              --->          |   |- JointsIds                    <shallow>
        |- Inverse Bind Matrices  --->          |   |- Inverse Bind Matrices        <shallow>
                                                |   |- Weights <from Primitive>     <unrolled>
                                                |   |- Joints  <from Primitive>     <unrolled>
                                                |
    - Node                                      |- Node  
        |- Camera                               |   |- Camera
        |- Mesh                                 |   |
                                                |   |- Index                        <from JointsIds>


    Mesh
    - Primitive
        - Attributes
            - POSITION
            - NORMAL
            - TANGENT
            - TEXCOORD_0
            - TEXCOORD_1
            - COLOR_0
            - JOINTS_0
            - WEIGHTS_0
        - Indices

# C interface (FBX)

    Model (.fbx)
        |- mesh
        |   |-  primitives
        |   |       |- vertices
        |   |       |- normals
        |   |       |- indices
        |   |       |- colors

    FBXModel                                                        <java Class>
        # String native getName()
        # String native getCreator()             
        # int native getVersion()
        # int native getMeshCount()
        # FBXMesh native getMesh(int index)
        # List<FBXMesh> native getMeshes()

    FBXMesh                                                         <java Class>
        # Buffer native getVertexBuffer(int handler, int mesh)
        # Buffer native getNormalsBuffer(int handler, int mesh)
        # Buffer native getIndexBuffer(int handler, int mesh)
        # Buffer native getColorsBuffer(int handler, int mesh)




