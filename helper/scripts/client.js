const avengersNames = ['Helper'];
let randomName = avengersNames[Math.floor(Math.random() * avengersNames.length)];

const main = async () => {
  /* Event handlers */

  // When a stream is added to the conference
  VoxeetSDK.conference.on('streamAdded', (participant, stream) => {
    if (stream.type === 'ScreenShare') {
      return addScreenShareNode(stream);
    }

    if (stream.getVideoTracks().length) {
      // Only add the video node if there is a video track
      addVideoNode(participant, stream);
    }

    addParticipantNode(participant);
  });

  // When a stream is updated
  VoxeetSDK.conference.on('streamUpdated', (participant, stream) => {
    if (stream.type === 'ScreenShare') return;

    if (stream.getVideoTracks().length) {
      // Only add the video node if there is a video track
      addVideoNode(participant, stream);
    } else {
      removeVideoNode(participant);
    }
  });

  // When a stream is removed from the conference
  VoxeetSDK.conference.on('streamRemoved', (participant, stream) => {
    if (stream.type === 'ScreenShare') {
      return removeScreenShareNode();
    }

    removeVideoNode(participant);
    removeParticipantNode(participant);
  });

  try {
    // Initialize the Voxeet SDK
    // WARNING: It is best practice to use the VoxeetSDK.initializeToken function to initialize the SDK.
    // Please read the documentation at:
    // https://dolby.io/developers/interactivity-apis/client-sdk/initializing
    VoxeetSDK.initialize('grB4NiWlMEvzpaLbBKBmVw==', 'ap6TnDQpnFUEPlIgrN3ir3hoL2NLrCLHLHd1s_YjYW0=');

    // Open a session for the user
    await VoxeetSDK.session.open({ name: randomName });

    // Initialize the UI
    initUI();
  } catch (e) {
    alert('Something went wrong : ' + e);
  }
}

main();
