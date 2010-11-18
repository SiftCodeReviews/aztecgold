package broker.object;

/**
 * The Interface provides the common methods for Application Objects on the server.
 */
public interface ServerObject extends DistributedObject {

	/**
	 * The method has to be implemented, it gets invoked on every request for the object. 
	 * It handles the object's internal calls to methods in order to provide the response
	 * to the request. If the request is a void request, that means it has no return value,
	 * the method shall return Null, if the request has a return value, the provided response
	 * Message shall be filled with the response and to trigger sending the response the response
	 * object has to be returned, so that the response parameter and the returning object are
	 * the same.
	 * 
	 * @param request Message containing the request
	 * @param response Message to be filled with the response value(s), addressing is provided
	 * @return parameter response in case a value shall be send back, Null in case of a void function. 
	 */
	public Message handle(Message request, Message response);

}
