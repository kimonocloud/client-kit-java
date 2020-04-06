package kimono.client.impl.csv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import kimono.client.KCDriverProperties;
import kimono.client.KCMapper;
import kimono.client.KCTenant;
import kimono.client.KCTopic;
import kimono.client.csv.KCFile;
import kimono.client.csv.KCFileManager;
import kimono.client.tasks.KCTask;

/**
 * Abstract base class implementation of {@link KCFileManager}
 */
public abstract class AbstractFileManager implements KCFileManager, AutoCloseable {

	public static final String OPT_ARCHIVE_FILES = "files.archive";
	
	/**
	 * Driver properties
	 */
	private KCDriverProperties props;
	
	/**
	 * The tenant
	 */
	private KCTenant tenant;
	
	/**
	 * The output folder: a sub-directory of the driver's output folder
	 * with a name equal to the tenant's id
	 */
	private File outputFolder;
	
	/**
	 * One or more Mappers to transform Kimono attributes as they're 
	 * written to files
	 * @see #setMapper(KCTopic, KCMapper)
	 */
	private Map<KCTopic,KCMapper> mappers = new HashMap<>();
	
	/**
	 * Files currently open for each topic. A {@link KCFile} wrapper is
	 * established when {@link #write(KCTask)} is called to write a row
	 * of data. Note {@link KCFile} implementations must append to existing
	 * files, and files may represent any form of storage including a
	 * database table.
	 */
	private Map<KCTopic,KCFile> files = new HashMap<>();
	
	/**
	 * The {@link KCFile} factory to use to create new instances
	 */
	private KCFileManager.Supplier fileSupplier;
	
	public AbstractFileManager( KCTenant tenant, KCDriverProperties props ) {
		this.props = props;
		this.tenant = tenant;
		outputFolder = new File(props.getOutputFolder(),tenant.getTenantInfo().getId().toString());
		outputFolder.mkdirs();
	}

	/**
	 * Set a {@link KCFileManager.Supplier} to produce {@link CFFile}
	 * @param supplier
	 */
	public void setFileSupplier( KCFileManager.Supplier supplier ) {
		fileSupplier = supplier;
	}
	
	@Override
	public KCTenant getTenant() {
		return tenant;
	}

	@Override
	public KCDriverProperties getProperties() {
		return props;
	}
	
	public void setMapper( KCTopic topic, KCMapper mapper ) {
		mappers.put(topic,mapper);
	}
	
	public KCMapper getMapper( KCTopic topic ) {
		return mappers.get(topic);
	}

	/**
	 * Get a {@link KCFile} instance for the data in a Task.
	 * 
	 * @param task A Data Event task
	 * @param create true to create a KCFile instance if one doesn't yet exist,
	 * 	false to return null in this case
	 * @return A {@link KCFile} instance or null
	 * @throws IOException
	 */
	protected KCFile getFileForTopic( KCTask task, boolean create ) throws IOException {
		KCTopic topic = task.getTopic();
		if( topic == null ) {
			return null;
		}
		KCFile file = files.get(topic);
		if( file == null && create ) {
			if( fileSupplier == null ) {
				throw new IllegalStateException("No registered file supplier");
			}
			file = fileSupplier.get(task.getGroupId(),topic);
			if( create ) {
				file.open();
			}
			files.put(topic,file);
		}
		return file;
	}
	
	/**
	 * Get all {@link KCFile}s that are associated with a group_id
	 */
	protected List<KCFile> getFilesInGroup( String groupId ) {
		return files.values().stream().filter(f->f.getGroupId().equals(groupId)).collect(Collectors.toList());
	}
	
	
	@Override
	public KCFile startGroup(KCTask task) throws IOException {
		return null;
	}

	@Override
	public void endGroup(KCTask task) {
		// Close the KCFile for this batch and remove from cache
		getFilesInGroup(task.getGroupId()).forEach(file->{
			file.close();
			files.remove(task.getTopic());
		});
	}
	
	@Override
	public void write(KCTask task) throws IOException {
		KCFile file = getFileForTopic(task,true);
		if( file != null ) {
			// Write mapped task attributes to the file
			JSONObject attrs = task.getAttributes();
			KCMapper mapper = getMapper(task.getTopic());
			file.write(task,mapper == null ? attrs : mapper.map(task, attrs));
		}
	}
	
	@Override
	public void close() throws IOException {
		files.values().forEach(f->f.close());
	}
	
	@Override
	public File getFolder() {
		return outputFolder;
	}
	
	public void setFolder( File folder ) {
		outputFolder = folder;
	}
	
	public File getArchiveFolder() {
		return new File(getFolder(),"archive");
	}
	
	@Override
	public void commitFiles() throws IOException {
		
		// Ask each file to commit
		for( KCFile file : files.values() ) {
			file.commit();
		}

		// Copy each file to the archive folder
		archiveFiles();
		
		// Delete files so the next set of files received is not appended to 
		// the existing set
		deleteFiles();
	}
	
	protected void archiveFiles() throws IOException {
		
		File archiveFolder = getArchiveFolder();
		if( archiveFolder.exists() ) {
			try {
				FileUtils.deleteDirectory(archiveFolder);
			} catch( IOException ioe ) {
				throw new IOException("Error clearing archive folder ("+archiveFolder.getAbsolutePath()+")",ioe);
			}
		} else {
			archiveFolder.mkdirs();
		}
		
		for( File srcFile : outputFolder.listFiles() ) {
			try {
				if( srcFile.isFile() ) {
					FileUtils.copyFile(srcFile, new File(archiveFolder,srcFile.getName()));
				}
			} catch( IOException ioe ) {
				throw new IOException("Error archiving file ("+srcFile+")",ioe);
			}
		}
	}
	
	protected void deleteFiles() {
		for( File srcFile : outputFolder.listFiles() ) {
			if( srcFile.isFile() ) {
				srcFile.delete();
			}
		}
	}
}
