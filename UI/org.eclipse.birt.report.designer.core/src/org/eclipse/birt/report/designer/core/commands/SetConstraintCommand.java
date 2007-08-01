/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.core.commands;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.report.designer.core.DesignerConstants;
import org.eclipse.birt.report.designer.core.model.SessionHandleAdapter;
import org.eclipse.birt.report.designer.core.model.schematic.HandleAdapterFactory;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.birt.report.designer.util.DEUtil;
import org.eclipse.birt.report.designer.util.MetricUtility;
import org.eclipse.birt.report.model.api.CommandStack;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.ImageHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.TableHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.metadata.DimensionValue;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * This command sets the constraint on an element to resize it.
 * 
 * 
 */

public class SetConstraintCommand extends Command
{
	private static Logger logger = Logger.getLogger( SetConstraintCommand.class.getName( ) );
	
	private static final String TRANS_LABEL_SET_CONSTRAINT = Messages.getString( "SetConstraintCommand.transLabel.setConstraint" ); //$NON-NLS-1$

	private ReportItemHandle model;

	/**
	 * constructor
	 */

	public SetConstraintCommand( )
	{
		super( Command_Label_Resize );
	}

	private static final String Command_Label_Resize = "Resize Command"; //$NON-NLS-1$

	private Dimension newSize;

	/**
	 * Executes the Command. This method should not be called if the Command is
	 * not executable.
	 */

	public void execute( )
	{
		CommandStack stack = SessionHandleAdapter.getInstance( )
				.getCommandStack( );
		// start trans
		stack.startTrans( TRANS_LABEL_SET_CONSTRAINT ); //$NON-NLS-1$

		try
		{
			if ( DesignerConstants.TRACING_COMMANDS )
			{
				System.out.println( "SetConstraintCommand >>  Starts. Target: " //$NON-NLS-1$
						+ DEUtil.getDisplayLabel( model ) + ",New size: " //$NON-NLS-1$
						+ newSize.width + "," //$NON-NLS-1$
						+ newSize.height );
			}
			if ( model instanceof TableHandle || model instanceof GridHandle )
			{
				HandleAdapterFactory.getInstance( )
						.getTableHandleAdapter( model )
						.ajustSize( newSize );
			}
			else if ( model instanceof ImageHandle )
			{
				int width = newSize.width;
				int height = newSize.height;

				if ( width <= 0 )
				{
					width = 1;
				}
				if ( height <= 0 )
				{
					height = 1;
				}
				DimensionValue dimensionValue = new DimensionValue( width,
						DesignChoiceConstants.UNITS_PX );
				model.getWidth( ).setValue( dimensionValue );
				dimensionValue = new DimensionValue( height,
						DesignChoiceConstants.UNITS_PX );
				model.getHeight( ).setValue( dimensionValue );
			}
			else
			{
				double width = MetricUtility.pixelToPixelInch( newSize.width );
				double height = MetricUtility.pixelToPixelInch( newSize.height );

				if ( width <= 0 )
				{
					width = 0.1;
				}
				if ( height <= 0 )
				{
					height = 0.1;
				}
				DimensionValue dimensionValue = new DimensionValue( width,
						DesignChoiceConstants.UNITS_IN );
				model.getWidth( ).setValue( dimensionValue );
				dimensionValue = new DimensionValue( height,
						DesignChoiceConstants.UNITS_IN );
				model.getHeight( ).setValue( dimensionValue );
			}
			stack.commit( );
			if ( DesignerConstants.TRACING_COMMANDS )
			{
				System.out.println( "SetConstraintCommand >> Finised." ); //$NON-NLS-1$
			}
		}
		catch ( SemanticException e )
		{
			if ( DesignerConstants.TRACING_COMMANDS )
			{
				System.out.println( "SetConstraintCommand >> Failed." ); //$NON-NLS-1$
			}
			logger.log( Level.SEVERE,e.getMessage( ), e);
			stack.rollback( );
		}
	}

	/**
	 * Gets the label
	 * 
	 * @return the label
	 */

	public String getLabel( )
	{
		return Command_Label_Resize;
	}

	/**
	 * Sets the constraint.
	 * 
	 * @param r
	 *            the rectangle
	 */

	public void setConstraint( Rectangle r )
	{
		setSize( r.getSize( ) );
	}

	/**
	 * Sets the part
	 * 
	 * @param part
	 *            the part
	 */

	public void setModel( ReportItemHandle model )
	{
		this.model = model;
	}

	/**
	 * Sets the size
	 * 
	 * @param p
	 *            the size
	 */

	public void setSize( Dimension p )
	{
		newSize = p;
	}

}