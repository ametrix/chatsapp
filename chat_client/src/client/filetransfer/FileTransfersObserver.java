/**
 * 
 */
package client.filetransfer;

/**
 * @author PDimitrov
 *
 */
public interface FileTransfersObserver {

	public void fileTransferCreated(FileTransfer rec);

	public void fileTransferStatusChanged(FileTransfer tr);

	public void fileTransferRemoved(FileTransfer tr);

}
