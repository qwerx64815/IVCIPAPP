package io.grpc.VoiceToText;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.13.1)",
    comments = "Source: VoiceToText.proto")
public final class TextGrpc {

  private TextGrpc() {}

  public static final String SERVICE_NAME = "VoiceToText.Text";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.grpc.VoiceToText.TextRequest,
          io.grpc.VoiceToText.command_list> getShowTextMethod;

  public static io.grpc.MethodDescriptor<io.grpc.VoiceToText.TextRequest,
          io.grpc.VoiceToText.command_list> getShowTextMethod() {
    io.grpc.MethodDescriptor<io.grpc.VoiceToText.TextRequest, io.grpc.VoiceToText.command_list> getShowTextMethod;
    if ((getShowTextMethod = TextGrpc.getShowTextMethod) == null) {
      synchronized (TextGrpc.class) {
        if ((getShowTextMethod = TextGrpc.getShowTextMethod) == null) {
          TextGrpc.getShowTextMethod = getShowTextMethod = 
              io.grpc.MethodDescriptor.<io.grpc.VoiceToText.TextRequest, io.grpc.VoiceToText.command_list>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "VoiceToText.Text", "ShowText"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.VoiceToText.TextRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.grpc.VoiceToText.command_list.getDefaultInstance()))
                  .setSchemaDescriptor(new TextMethodDescriptorSupplier("ShowText"))
                  .build();
          }
        }
     }
     return getShowTextMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TextStub newStub(io.grpc.Channel channel) {
    return new TextStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TextBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new TextBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TextFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new TextFutureStub(channel);
  }

  /**
   */
  public static abstract class TextImplBase implements io.grpc.BindableService {

    /**
     */
    public void showText(io.grpc.VoiceToText.TextRequest request,
                         io.grpc.stub.StreamObserver<io.grpc.VoiceToText.command_list> responseObserver) {
      asyncUnimplementedUnaryCall(getShowTextMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getShowTextMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                      io.grpc.VoiceToText.TextRequest,
                      io.grpc.VoiceToText.command_list>(
                  this, METHODID_SHOW_TEXT)))
          .build();
    }
  }

  /**
   */
  public static final class TextStub extends io.grpc.stub.AbstractStub<TextStub> {
    private TextStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TextStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TextStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TextStub(channel, callOptions);
    }

    /**
     */
    public void showText(io.grpc.VoiceToText.TextRequest request,
                         io.grpc.stub.StreamObserver<io.grpc.VoiceToText.command_list> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getShowTextMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TextBlockingStub extends io.grpc.stub.AbstractStub<TextBlockingStub> {
    private TextBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TextBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TextBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TextBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.VoiceToText.command_list showText(io.grpc.VoiceToText.TextRequest request) {
      return blockingUnaryCall(
          getChannel(), getShowTextMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TextFutureStub extends io.grpc.stub.AbstractStub<TextFutureStub> {
    private TextFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private TextFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected TextFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TextFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.VoiceToText.command_list> showText(
        io.grpc.VoiceToText.TextRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getShowTextMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SHOW_TEXT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TextImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TextImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SHOW_TEXT:
          serviceImpl.showText((io.grpc.VoiceToText.TextRequest) request,
              (io.grpc.stub.StreamObserver<io.grpc.VoiceToText.command_list>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class TextBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TextBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return VoiceToTextProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Text");
    }
  }

  private static final class TextFileDescriptorSupplier
      extends TextBaseDescriptorSupplier {
    TextFileDescriptorSupplier() {}
  }

  private static final class TextMethodDescriptorSupplier
      extends TextBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TextMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TextGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TextFileDescriptorSupplier())
              .addMethod(getShowTextMethod())
              .build();
        }
      }
    }
    return result;
  }
}
