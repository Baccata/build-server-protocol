$version: "2"

namespace bsp

// Applying the tags a-posteriori to avoid hurting the readability of the spec
apply URI @tags(["basic"])

apply URIs @tags(["basic"])

apply Json @tags(["basic"])

apply BuildTargetIdentifier @tags(["basic"])

apply BuildTargetIdentifiers @tags(["basic"])

apply BuildTarget @tags(["basic"])

apply BuildTargetDataKind @tags(["basic"])

apply StringList @tags(["basic"])

apply BuildTargetCapabilities @tags(["basic"])

apply BuildTargetTag @tags(["basic"])

apply TaskId @tags(["basic"])

apply Identifier @tags(["basic"])

apply Identifiers @tags(["basic"])

apply JvmBuildTarget @tags(["scala"])

apply ScalaBuildTarget @tags(["scala"])

apply ScalaPlatform @tags(["scala"])
